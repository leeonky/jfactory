package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.jfactory.Instance;
import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.util.BeanClass;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.leeonky.dal.Assertions.expect;
import static java.util.Arrays.asList;

public class IntegrationTestContext {
    private final List<String> beanCodes = new ArrayList<>();
    private final List<String> specCodes = new ArrayList<>();
    private final List<Class> classes = new ArrayList<>();
    private final JFactory jFactory = new JFactory();
    private final List<Runnable> register = new ArrayList<>();
    private final Compiler compiler = new Compiler();
    private int snippetIndex = 0;
    private Object bean;

    public void givenBean(String classCode) {
        beanCodes.add(classCode);
    }

    public void givenTypeSpec(String type, String specCode) {
        register.add(() -> jFactory.factory(getType(type)).spec(defineSpec(specCode)));
    }


    @SneakyThrows
    private Consumer<Instance<?>> defineSpec(String specCode) {
        String className = "Snip" + (snippetIndex++);
        String snipCode = "package src.test;" +
                "import java.util.function.*;" +
                "import java.util.*;" +
                "import com.github.leeonky.jfactory.*;" +
                "public class " + className + " implements Consumer<Instance<?>> {\n" +
                "    @Override\n" +
                "    public void accept(Instance<?> instance) {" + specCode + "}\n" +
                "}";

        return (Consumer<Instance<?>>) compiler.compileToClasses(asList(snipCode)).get(0).newInstance();
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
        specCodes.add(snipCode);
        return className;
    }

    public void givenTypeTrait(String type, String trait, String codeSnippet) {
        register.add(() -> jFactory.factory(getType(type)).spec(trait, defineSpec(codeSnippet)));
    }

    private Class getType(String className) {
        Class type = classes.stream().filter(clazz -> clazz.getSimpleName().equals(className))
                .findFirst().orElseThrow(() -> new IllegalArgumentException
                        ("cannot find bean class: " + className + "\nclasses: " + classes));
        return type;
    }

    private void compileAll() {
        if (classes.isEmpty()) {
            classes.addAll(compiler.compileToClasses(Stream.concat(beanCodes.stream(), specCodes.stream()).map(s ->
                    "package src.test;" + "import com.github.leeonky.jfactory.*;\n" +
                            "import com.github.leeonky.jfactory.*;\n" +
                            "import java.math.*;\n" + s).collect(Collectors.toList())));
            classes.stream().filter(Spec.class::isAssignableFrom).forEach(jFactory::register);
        }
    }

    public void create(String type, String[] traits) {
        create(() -> jFactory.type(getType(type)).traits(traits).create());
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
        specCodes.add(specClass);
    }

    public void createSpec(String[] specTraits) {
        create(() -> jFactory.createAs(specTraits));
    }

    public void createSpecWithSnippet(String spec, String traitSnippet) {
        String tmpClass = specTraitMothodCall(spec, traitSnippet);
        create(() -> jFactory.createAs(getType(spec), (Consumer) BeanClass.create(getType(tmpClass)).newInstance()));
    }
}
