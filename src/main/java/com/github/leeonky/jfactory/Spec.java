package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.github.leeonky.jfactory.PropertyChain.createChain;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public class Spec<T> {
    private final List<BiConsumer<JFactory, ObjectProducer<T>>> operations = new ArrayList<>();
    private final Set<PropertySpec<T>.IsSpec<?, ? extends Spec<?>>> invalidIsSpecs = new LinkedHashSet<>();

    private Instance<T> instance;
    private Class<T> type = null;

    public void main() {
    }

    public PropertySpec<T> property(String property) {
        return new PropertySpec<>(this, createChain(property));
    }

    Spec<T> append(BiConsumer<JFactory, ObjectProducer<T>> operation) {
        operations.add(operation);
        return this;
    }

    void apply(JFactory jFactory, ObjectProducer<T> producer) {
        operations.forEach(o -> o.accept(jFactory, producer));
        type = producer.getType().getType();
        if (!invalidIsSpecs.isEmpty())
            throw new InvalidSpecException("Invalid property spec:\n\t"
                    + invalidIsSpecs.stream().map(PropertySpec.IsSpec::getPosition).collect(Collectors.joining("\n\t"))
                    + "\nShould finish method chain with `and` or `which`:\n"
                    + "\tproperty().from().which()\n"
                    + "\tproperty().from().and()\n"
                    + "Or use property().is() to create object with only spec directly.");
    }

    @SuppressWarnings("unchecked")
    public Class<T> getType() {
        return getClass().equals(Spec.class) ? type :
                (Class<T>) BeanClass.create(getClass()).getSuper(Spec.class).getTypeArguments(0)
                        .orElseThrow(() -> new IllegalStateException("Cannot guess type via generic type argument, please override Spec::getType"))
                        .getType();
    }

    protected String getName() {
        return getClass().getSimpleName();
    }

    public Spec<T> link(String property, String... others) {
        List<PropertyChain> linkProperties = concat(of(property), of(others)).map(PropertyChain::createChain).collect(toList());
        append((jFactory, objectProducer) -> objectProducer.link(linkProperties));
        return this;
    }

    Spec<T> setInstance(Instance<T> instance) {
        this.instance = instance;
        return this;
    }

    public <P> P param(String key) {
        return instance.param(key);
    }

    public <P> P param(String key, P defaultValue) {
        return instance.param(key, defaultValue);
    }

    public Arguments params(String property) {
        return instance.params(property);
    }

    public Arguments params() {
        return instance.params();
    }

    public Instance<T> instance() {
        return instance;
    }

    <V, S extends Spec<V>> PropertySpec<T>.IsSpec<V, S> newIsSpec(Class<S> specClass, PropertySpec<T> propertySpec) {
        PropertySpec<T>.IsSpec<V, S> isSpec = propertySpec.new IsSpec<V, S>(specClass);
        invalidIsSpecs.add(isSpec);
        return isSpec;
    }

    void consume(PropertySpec<T>.IsSpec<?, ? extends Spec<?>> isSpec) {
        invalidIsSpecs.remove(isSpec);
    }
}
