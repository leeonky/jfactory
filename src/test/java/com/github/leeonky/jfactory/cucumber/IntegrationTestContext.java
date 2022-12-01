package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.JavaCompiler;
import com.github.leeonky.util.JavaCompilerPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.leeonky.dal.Assertions.expect;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTestContext {
    private final Map<String, String> classCodes = new HashMap<>();
    private final List<String> registers = new ArrayList<>();
    private final List<Class> classes = new ArrayList<>();
    private final List<Runnable> register = new ArrayList<>();

    public static int threadsCount(String env, int defaultValue) {
        String value = System.getenv(env);
        if (value == null)
            return defaultValue;
        return Integer.parseInt(value);
    }

    private static final JavaCompilerPool JAVA_COMPILER_POOL =
            new JavaCompilerPool(threadsCount("COMPILER_THREAD_SIZE", 8) * 2, "src.test.generate.ws");
    private final JavaCompiler compiler = JAVA_COMPILER_POOL.take();
    private List list;
    private JFactory jFactory = new JFactory();
    private int snippetIndex = 0;
    private Object bean;
    private Throwable throwable;

    public void releaseCompiler() {
        JAVA_COMPILER_POOL.giveBack(compiler);
    }

    private <T> T createProcedure(Class<T> type, String tmpClass) {
        return (T) BeanClass.create(getType(tmpClass)).newInstance();
    }

    public void givenBean(String classCode) {
        addClass(classCode);
    }

    private Class getType(String className) {
        Class type = classes.stream().filter(clazz -> clazz.getSimpleName().equals(className))
                .findFirst().orElseThrow(() -> new IllegalArgumentException
                        ("cannot find bean class: " + className + "\nclasses: " + classes));
        return type;
    }

    private String jFactoryOperate(String builderSnippet) {
        String className = "Snip" + (snippetIndex++);
        String snipCode = "import java.util.function.*;\n" +
                "import java.util.*;\n" +
                "import com.github.leeonky.util.*;\n" +
                "import com.github.leeonky.jfactory.*;\n" +
                "import static com.github.leeonky.jfactory.ArgumentMapFactory.arg;\n" +
                "public class " + className + " implements Consumer<JFactory> {\n" +
                "    @Override\n" +
                "    public void accept(JFactory jFactory) { " + builderSnippet + ";}\n" +
                "}";
        addClass(snipCode);
        return className;
    }

    private void addClass(String snipCode) {
        classCodes.put(JavaCompiler.guessClassName(snipCode), snipCode);
    }

    private void compileAll() {
        classes.clear();
        classes.addAll(compiler.compileToClasses(classCodes.values().stream().map(s -> "import com.github.leeonky.jfactory.*;\n" +
                "import java.util.function.*;\n" +
                "import java.util.*;\n" +
                "import java.math.*;\n" + s).collect(Collectors.toList())));
        classes.stream().filter(Spec.class::isAssignableFrom).forEach(jFactory::register);
    }

    public void register(String factorySnippet) {
        registers.add(factorySnippet);
    }

    private void create(Supplier<Object> supplier) {
        try {
            compileAll();
            register.forEach(Runnable::run);
            bean = supplier.get();
        } catch (Throwable throwable) {
            this.throwable = throwable;
        }
    }

    public void verify(String dal) throws Throwable {
        if (throwable != null)
            throw throwable;
        expect(bean).should(dal);
    }

    public void specClass(String specClass) {
        addClass(specClass);
    }

    @Deprecated
    public void operate(String operateSnippet) {
        String tmpClass = jFactoryOperate(operateSnippet);
        register.add(() -> createProcedure(Consumer.class, tmpClass).accept(jFactory));
    }

    public void shouldThrow(String dal) {
        expect(throwable).should(dal.replace("#package#", compiler.packagePrefix()));
    }

    public void build(String builderSnippet) {
        String tmpClass = jFactoryAction(builderSnippet);
        create(() -> createProcedure(Function.class, tmpClass).apply(jFactory));
    }

    private String jFactoryAction(String builderSnippet) {
        String className = "Snip" + (snippetIndex++);
        String snipCode = "import java.util.function.*;\n" +
                "import java.util.*;\n" +
                "import com.github.leeonky.util.*;\n" +
                "import com.github.leeonky.jfactory.*;\n" +
                "import static com.github.leeonky.jfactory.ArgumentMapFactory.arg;\n" +
                "public class " + className + " implements Function<JFactory, Object> {\n" +
                "    @Override\n" +
                "    public Object apply(JFactory jFactory) {\n" +
                String.join("\n", registers) + "\n" +
                " return " + builderSnippet + "}\n" +
                "}";
        addClass(snipCode);
        return className;
    }


    private String createObject(String declaration) {
        String className = "Snip" + (snippetIndex++);
        return "import java.util.function.*;\n" +
                "import java.util.*;\n" +
                "import com.github.leeonky.util.*;\n" +
                "import com.github.leeonky.jfactory.*;\n" +
                "import static com.github.leeonky.jfactory.ArgumentMapFactory.arg;\n" +
                "public class " + className + " implements Function<List<Object>, Object> {\n" +
                "    @Override\n" +
                "    public Object apply(List<Object> list) { return " + declaration + "}\n" +
                "}";
    }

    public void declare(String declaration) {
        jFactory = (JFactory) ((Function) BeanClass.create(compiler.
                compileToClasses(asList(createObject(declaration))).get(0)).newInstance()).apply(list);
    }

    public void declareList(String listDeclaration) {
        list = (List) ((Function) BeanClass.create(compiler.
                compileToClasses(asList(createObject(listDeclaration))).get(0)).newInstance()).apply(null);
    }

    public void listShould(String dal) {
        assertThat(throwable).isNull();
        expect(list).should(dal);
    }
}
