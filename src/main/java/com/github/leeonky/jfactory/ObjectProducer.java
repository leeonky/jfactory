package com.github.leeonky.jfactory;

import com.github.leeonky.util.PropertyWriter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.leeonky.jfactory.PropertyChain.createChain;
import static com.github.leeonky.util.BeanClass.create;
import static java.util.stream.IntStream.range;

class ObjectProducer<T> extends Producer<T> {
    private final ObjectFactory<T> factory;
    private final JFactory jFactory;
    private final DefaultBuilder<T> builder;
    private final RootInstance<T> instance;
    private final Map<String, Producer<?>> children = new HashMap<>();
    private final Map<PropertyChain, Dependency<?>> dependencies = new LinkedHashMap<>();
    private final Map<PropertyChain, String> reverseAssociations = new LinkedHashMap<>();
    private final LinkCollection linkCollection = new LinkCollection();
    private final ListPersistable cachedChildren = new ListPersistable();
    private final Set<String> ignorePropertiesInSpec = new HashSet<>();
    private Persistable persistable;

    public ObjectProducer(JFactory jFactory, ObjectFactory<T> factory, DefaultBuilder<T> builder) {
        super(factory.getType());
        this.factory = factory;
        this.jFactory = jFactory;
        this.builder = builder;
        instance = factory.createInstance(builder.getArguments());
        persistable = jFactory.getDataRepository();
        createDefaultValueProducers();
        builder.processSpecAndInputProperty(this, instance);
        createElementDefaultValueProducers();
        setupReverseAssociations();
        resolveBuilderProducers();
    }

    protected void resolveBuilderProducers() {
        List<Map.Entry<String, Producer<?>>> buildValueProducers = children.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof BuilderValueProducer).collect(Collectors.toList());
        buildValueProducers.forEach(e -> setChild(e.getKey(), ((BuilderValueProducer) e.getValue()).getProducer()));
    }

    private void createElementDefaultValueProducers() {
        range(0, instance.collectionSize()).mapToObj(String::valueOf).filter(index -> children.get(index) == null)
                .map(index -> getType().getPropertyWriter(index)).forEach((PropertyWriter<T> propertyWriter) ->
                        setChild(propertyWriter.getName(), new DefaultValueFactoryProducer<>(factory.getType(),
                                factory.getFactorySet().getDefaultValueBuilder(propertyWriter.getType()),
                                instance.sub(propertyWriter))
                        ));
    }

    private void setupReverseAssociations() {
        reverseAssociations.forEach((child, association) ->
                descendant(child).setupAssociation(association, instance, cachedChildren));
    }

    @Override
    public void setChild(String property, Producer<?> producer) {
        children.put(property, producer);
    }

    @Override
    public Producer<?> childOrDefault(String property) {
        Producer<?> producer = children.get(property);
        if (producer == null)
            producer = addDefaultCollectionProducer(getType().getPropertyWriter(property));
        return producer;
    }

    private Producer<?> addDefaultCollectionProducer(PropertyWriter<?> property) {
        Producer<?> result = null;
        if (property.getType().isCollection())
            setChild(property.getName(), result = new CollectionProducer<>(getType(), property.getType(),
                    instance.sub(property), factory.getFactorySet()));
        return result;
    }

    @Override
    protected T produce() {
        return instance.cache(() -> factory.create(instance), obj -> {
            produceSubs(obj);
            persistable.save(obj);
            cachedChildren.getAll().forEach(persistable::save);
        });
    }

    private void produceSubs(T obj) {
        children.entrySet().stream().filter(this::isDefaultValueProducer).forEach(e -> produceSub(obj, e));
        children.entrySet().stream().filter(e -> !(isDefaultValueProducer(e))).forEach(e -> produceSub(obj, e));
    }

    private void produceSub(T obj, Map.Entry<String, Producer<?>> e) {
        getType().setPropertyValue(obj, e.getKey(), e.getValue().getValue());
    }

    private boolean isDefaultValueProducer(Map.Entry<String, Producer<?>> e) {
        return e.getValue() instanceof DefaultValueFactoryProducer;
    }

    @Override
    public Optional<Producer<?>> child(String property) {
        return Optional.ofNullable(children.get(property));
    }

    public void addDependency(PropertyChain property, Function<Object[], Object> rule, List<PropertyChain> dependencies) {
        this.dependencies.put(property, new Dependency<>(property, dependencies, rule));
    }

    public ObjectProducer<T> doDependenciesAndLinks() {
        doDependencies();
        doLinks(this, createChain(""));
        return this;
    }

    @Override
    protected void doLinks(Producer<?> root, PropertyChain current) {
        children.forEach((property, producer) -> producer.doLinks(root, current.concat(property)));
        linkCollection.processLinks(root, current);
    }

    @Override
    protected void doDependencies() {
        children.values().forEach(Producer::doDependencies);
        dependencies.values().forEach(dependency -> dependency.process(this));
    }

    public void link(List<PropertyChain> properties) {
        linkCollection.link(properties);
    }

    private void createDefaultValueProducers() {
        getType().getPropertyWriters().values().stream().filter(jFactory::shouldCreateDefaultValue)
                .forEach(propertyWriter -> subDefaultValueProducer(propertyWriter)
                        .ifPresent(producer -> setChild(propertyWriter.getName(), producer)));
    }

    @Override
    public Optional<Producer> subDefaultValueProducer(PropertyWriter<?> property) {
        return factory.getFactorySet().queryDefaultValueBuilder(property.getType())
                .map(builder -> new DefaultValueFactoryProducer<>(getType(), builder, instance.sub(property)));
    }

    @Override
    public Producer<T> changeTo(Producer<T> newProducer) {
        return newProducer.changeFrom(this);
    }

    @Override
    protected Producer<T> changeFrom(ObjectProducer<T> origin) {
        return origin.builder.marge(builder).createProducer();
    }

    public void appendReverseAssociation(PropertyChain property, String association) {
        reverseAssociations.put(property, association);
    }

    @Override
    protected <T> void setupAssociation(String association, RootInstance<T> instance, ListPersistable cachedChildren) {
        setChild(association, new UnFixedValueProducer<>(instance.reference(), create(instance.spec().getType())));
        persistable = cachedChildren;
    }

    public boolean isReverseAssociation(String property) {
        return reverseAssociations.containsKey(PropertyChain.createChain(property));
    }

    public void ignoreProperty(String property) {
        ignorePropertiesInSpec.add(property);
    }

    public void processSpecIgnoreProperties() {
        children.entrySet().stream().filter(e -> e.getValue() instanceof DefaultValueProducer
                        && ignorePropertiesInSpec.contains(e.getKey())).map(Map.Entry::getKey).collect(Collectors.toList())
                .forEach(children::remove);
    }

    @Override
    protected boolean isFixed() {
        return children.values().stream().anyMatch(Producer::isFixed);
    }
}
