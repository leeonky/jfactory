package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.jfactory.Builder;
import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.util.BeanClass;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
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
    private Throwable throwable;

    private <T> T createProcedure(Class<T> type, String tmpClass) {
        return (T) BeanClass.create(getType(tmpClass)).newInstance();
    }

    public void givenBean(String classCode) {
        classCodes.add(classCode);
    }

    private Class getType(String className) {
        Class type = classes.stream().filter(clazz -> clazz.getSimpleName().equals(className))
                .findFirst().orElseThrow(() -> new IllegalArgumentException
                        ("cannot find bean class: " + className + "\nclasses: " + classes));
        return type;
    }

    private String jFactoryAction(String builderSnippet) {
        String className = "Snip" + (snippetIndex++);
        String snipCode = "import java.util.function.*;" +
                "import java.util.*;" +
                "import com.github.leeonky.util.*;" +
                "import com.github.leeonky.jfactory.*;" +
                "import static com.github.leeonky.jfactory.ArgumentMapFactory.arg;" +
                "public class " + className + " implements Function<JFactory, Object> {\n" +
                "    @Override\n" +
                "    public Object apply(JFactory jfactory) { return jfactory." + builderSnippet + ";}\n" +
                "}";
        classCodes.add(snipCode);
        return className;
    }

    private String jFactoryOperate(String builderSnippet) {
        String className = "Snip" + (snippetIndex++);
        String snipCode = "import java.util.function.*;" +
                "import java.util.*;" +
                "import com.github.leeonky.util.*;" +
                "import com.github.leeonky.jfactory.*;" +
                "import static com.github.leeonky.jfactory.ArgumentMapFactory.arg;" +
                "public class " + className + " implements Consumer<JFactory> {\n" +
                "    @Override\n" +
                "    public void accept(JFactory jfactory) { jfactory." + builderSnippet + ";}\n" +
                "}";
        classCodes.add(snipCode);
        return className;
    }

    private void compileAll() {
        classes.clear();
        classes.addAll(compiler.compileToClasses(classCodes.stream().map(s -> "package src.test;\n" +
                "import com.github.leeonky.jfactory.*;\n" +
                "import java.util.function.*;\n" +
                "import java.util.*;\n" +
                "import java.math.*;\n" + s).collect(Collectors.toList())));
        classes.stream().filter(Spec.class::isAssignableFrom).forEach(jFactory::register);
    }

    public void create(String builderSnippet) {
        String tmpClass = jFactoryAction(builderSnippet);
        create(() -> {
            try {
                return ((Builder) createProcedure(Function.class, tmpClass).apply(jFactory)).create();
            } catch (Throwable throwable) {
                this.throwable = throwable;
                return null;
            }
        });
    }

    public void register(String factorySnippet) {
        String tmpClass = jFactoryAction(factorySnippet);
        register.add(() -> createProcedure(Function.class, tmpClass).apply(jFactory));
    }

    private void create(Supplier<Object> supplier) {
        compileAll();
        register.forEach(Runnable::run);
        try {
            bean = supplier.get();
        } catch (Throwable throwable) {
            this.throwable = throwable;
        }
    }

    public void verifyBean(String dal) {
        expect(bean).should(dal);
    }

    public void specClass(String specClass) {
        classCodes.add(specClass);
    }

    public void execute(String exeSnippet) {
        String tmpClass = jFactoryAction(exeSnippet);
        create(() -> createProcedure(Function.class, tmpClass).apply(jFactory));
    }

    public void query(String builderSnippet) {
        String tmpClass = jFactoryAction(builderSnippet);
        create(() -> ((Builder) createProcedure(Function.class, tmpClass).apply(jFactory)).query());
    }

    public void queryAll(String builderSnippet) {
        String tmpClass = jFactoryAction(builderSnippet);
        create(() -> ((Builder) createProcedure(Function.class, tmpClass).apply(jFactory)).queryAll());
    }

    public void operate(String operateSnippet) {
        String tmpClass = jFactoryOperate(operateSnippet);
        register.add(() -> createProcedure(Consumer.class, tmpClass).accept(jFactory));
    }

    public void shouldThrow(String dal) {
        expect(throwable).should(dal);
    }

    public void createAs(String createAs) {
        String tmpClass = jFactoryAction(createAs);
        create(() -> createProcedure(Function.class, tmpClass).apply(jFactory));
    }
}
