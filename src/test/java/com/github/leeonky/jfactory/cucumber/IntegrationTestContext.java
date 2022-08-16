package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.jfactory.Builder;
import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.util.BeanClass;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.leeonky.dal.Assertions.expect;

public class IntegrationTestContext {
    private final List<String> classCodes = new ArrayList<>();
    private final List<Class> classes = new ArrayList<>();
    private final JFactory jFactory = new JFactory();
    private final List<Runnable> register = new ArrayList<>();
    private final Compiler compiler = new Compiler();
    private int snippetIndex = 0;
    private Object bean;

    public void givenBean(String classCode) {
        classCodes.add(classCode);
    }

    private String specTraitMothodCall(String spec, String specCode) {
        String className = "Snip" + (snippetIndex++);
        String snipCode = "import java.util.function.*;" +
                "import java.util.*;" +
                "import com.github.leeonky.jfactory.*;" +
                "public class " + className + " implements Consumer<" + spec + "> {\n" +
                "    @Override\n" +
                "    public void accept(" + spec + " spec) {" + specCode + ";}\n" +
                "}";
        classCodes.add(snipCode);
        return className;
    }

    private Class getType(String className) {
        Class type = classes.stream().filter(clazz -> clazz.getSimpleName().equals(className))
                .findFirst().orElseThrow(() -> new IllegalArgumentException
                        ("cannot find bean class: " + className + "\nclasses: " + classes));
        return type;
    }

    private void compileAll() {
        if (classes.isEmpty()) {
            classes.addAll(compiler.compileToClasses(classCodes.stream().map(s -> "package src.test;\n" +
                    "import com.github.leeonky.jfactory.*;\n" +
                    "import java.util.function.*;\n" +
                    "import java.util.*;\n" +
                    "import java.math.*;\n" + s).collect(Collectors.toList())));
            classes.stream().filter(Spec.class::isAssignableFrom).forEach(jFactory::register);
        }
    }

    public void create(String type, String[] traits, Map<String, ?> properties) {
        create(() -> setProperties(jFactory.type(getType(type)).traits(traits), properties).create());
    }

    public Builder setProperties(Builder builder, Map<String, ?> properties) {
        switch (properties.size()) {
            case 0:
                return builder;
            case 1:
                return builder.property(properties.keySet().iterator().next(),
                        properties.values().iterator().next());
            default:
                return builder.properties(properties);
        }
    }

    public Builder setParams(Builder builder, Map<String, ?> params) {
        switch (params.size()) {
            case 0:
                return builder;
            case 1:
                return builder.arg(params.keySet().iterator().next(),
                        params.values().iterator().next());
            default:
                return builder.args(params);
        }
    }

    private void create(Supplier<Object> supplier) {
        compileAll();
        register.forEach(Runnable::run);
        bean = supplier.get();
    }

    public void verifyBean(String dal) {
        expect(bean).should(dal);
    }

    public void specClass(String specClass) {
        classCodes.add(specClass);
    }

    public void createSpec(String[] specTraits) {
        create(() -> jFactory.createAs(specTraits));
    }

    public void createSpec(String specTraits, Map<String, String> properties) {
        create(() -> jFactory.spec(specTraits).properties(properties).create());
    }

    public void createSpecWithSnippet(String spec, String traitSnippet) {
        String tmpClass = specTraitMothodCall(spec, traitSnippet);
        create(() -> jFactory.createAs(getType(spec), (Consumer) createProcedure(tmpClass)));
    }

    @SneakyThrows
    private String registerJFactoryCode(String snippet) {
        String className = "Snip" + (snippetIndex++);
        String snipCode = "public class " + className + " implements Consumer<JFactory> {\n" +
                "    @Override\n" +
                "    public void accept(JFactory jfactory) {" + snippet + "}\n" +
                "}";
        classCodes.add(snipCode);
        return className;
    }

    public void registerJfactory(String registerCode) {
        String tmpClass = registerJFactoryCode(registerCode);
        register.add(() -> ((Consumer) createProcedure(tmpClass)).accept(jFactory));
    }

    private Object createProcedure(String tmpClass) {
        return BeanClass.create(getType(tmpClass)).newInstance();
    }

    public void create(String type, String[] traits, Map<String, ?> properties, Map<String, ?> params) {
        create(() -> setParams(setProperties(jFactory.type(getType(type)).traits(traits), properties), params).create());
    }
}
