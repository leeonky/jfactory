package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.jfactory.Instance;
import com.github.leeonky.jfactory.JFactory;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.github.leeonky.dal.Assertions.expect;
import static java.util.Arrays.asList;

public class IntegrationTestContext {
    private final List<String> beanCodes = new ArrayList<>();
    private final List<Class<?>> classes = new ArrayList<>();
    private final JFactory jFactory = new JFactory();
    private final List<Runnable> register = new ArrayList<>();
    private final Compiler compiler = new Compiler();
    private int snippetIndex = 0;
    private Object bean;

    public void givenBean(String classCode) {
        beanCodes.add(classCode);
    }

    public void givenTypeSpec(String type, String specCode) {
        register.add(() -> jFactory.factory(getType(type)).spec(buildSnipet(specCode)));
    }


    @SneakyThrows
    private Consumer<Instance<?>> buildSnipet(String specCode) {
        String className = "Snip" + (snippetIndex++);
        String snipCode = "import java.util.function.*;" +
                "import java.util.*;" +
                "import com.github.leeonky.jfactory.*;" +
                "public class " + className + " implements Consumer<Instance<?>> {\n" +
                "    @Override\n" +
                "    public void accept(Instance<?> instance) {" + specCode + "}\n" +
                "}";

        List<Class<?>> result = compiler.compileToClasses(asList(snipCode));
        Consumer<Instance<?>> consumer = (Consumer<Instance<?>>) result.get(0).newInstance();
        return consumer;
    }

    public void givenTypeTrait(String type, String trait, String codeSnippet) {
        register.add(() -> jFactory.factory(getType(type)).spec(trait, buildSnipet(codeSnippet)));
    }

    private Class getType(String className) {
        Class type = classes.stream().filter(clazz -> clazz.getName().equals(className))
                .findFirst().orElseThrow(() -> new IllegalArgumentException
                        ("cannot find bean class: " + className + "\nclasses: " + classes));
        return type;
    }

    private void compileAll() {
        if (classes.isEmpty()) {
            classes.addAll(compiler.compileToClasses(beanCodes.stream().map(s ->
                    "import com.github.leeonky.jfactory.*;\n" +
                            "import com.github.leeonky.jfactory.*;\n" +
                            "import java.math.*;\n" + s).collect(Collectors.toList())));
        }
    }

    public void create(String type, String[] traits) {
        compileAll();
        register.forEach(Runnable::run);
        bean = jFactory.type(getType(type)).traits(traits).create();
    }

    public void verifyBean(String dal) {
        expect(bean).should(dal);
    }
}
