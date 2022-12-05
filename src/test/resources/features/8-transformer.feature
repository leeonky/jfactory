Feature: transformer

  Background:
    Given declaration jFactory =
      """
      new JFactory();
      """
    Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """

  Rule: single creation, define in type

    Scenario: define use transformer by type
      And register:
      """
      jFactory.factory(Bean.class).transformer("value", String::toUpperCase);
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: HELLO
      """
      When the following spec class:
      """
      public class AnySpec extends Spec<Bean> {
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: HELLO
      """

    Scenario: define use transformer by super type
      Given the following bean class:
      """
      public class SubBean extends Bean {
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).transformer("value", String::toUpperCase);
      """
      When build:
      """
      jFactory.type(SubBean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: HELLO
      """
      When the following spec class:
      """
      public class AnySpec extends Spec<Bean> {
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: HELLO
      """

    Scenario: override transformer in sub type
      Given the following bean class:
      """
      public class SubBean extends Bean {
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).transformer("value", String::toUpperCase);
      """
      And register:
      """
      jFactory.factory(SubBean.class).transformer("value", s -> "(" + s + ")");
      """
      When build:
      """
      jFactory.type(SubBean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: '(hello)'
      """

    Scenario: define in type and use in spec
      And register:
      """
      jFactory.factory(Bean.class).transformer("value", String::toUpperCase);
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
      }
      """
      When build:
      """
      jFactory.spec(ABean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: HELLO
      """
      And the following spec class:
      """
      public class AnotherBean extends Spec<Bean> {
      }
      """
      When build:
      """
      jFactory.spec(AnotherBean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: HELLO
      """

    Scenario: define in type and use in sub spec
      And register:
      """
      jFactory.factory(Bean.class).transformer("value", String::toUpperCase);
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
      }
      """
      And the following spec class:
      """
      public class SubABean extends ABean {
      }
      """
      When build:
      """
      jFactory.spec(SubABean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: HELLO
      """

    Scenario: not match transformer
      And register:
      """
      jFactory.factory(Bean.class).transformer("value", new Transformer() {
          @Override
          public Object transform(String input) {
              throw new RuntimeException();
          }

          @Override
          public boolean matches(String input) {
              return false;
          }
      });
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: hello
      """

    Scenario: list property
      Given the following bean class:
      """
      public class Bean {
        public List<String> value;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).transformer("value", input -> input.split(","));
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", "a,b,c").create();
      """
      Then the result should:
      """
      value: [a b c]
      """

    Scenario: define in type but override in spec
      And register:
      """
      jFactory.factory(Bean.class).transformer("value", String::toUpperCase);
      """
      And the following spec class:
      """
      @Global
      public class ABean extends Spec<Bean> {
      }
      """
      And register:
      """
      jFactory.specFactory(ABean.class).transformer("value", str -> "(" + str + ")");
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: '(hello)'
      """
      Given the following bean class:
      """
      public class SubBean extends Bean {
      }
      """
      When build:
      """
      jFactory.type(SubBean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: '(hello)'
      """
      And the following spec class:
      """
      public class AnotherBean extends Spec<Bean> {
      }
      """
      When build:
      """
      jFactory.spec(AnotherBean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: '(hello)'
      """
      When build:
      """
      jFactory.spec(ABean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: '(hello)'
      """

  Rule: single creation, define in spec

    Scenario: define use transformer by spec, sub spec
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
      }
      """
      And register:
      """
      jFactory.specFactory(ABean.class).transformer("value", String::toUpperCase);
      """
      When build:
      """
      jFactory.spec(ABean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: HELLO
      """
      And the following spec class:
      """
      public class SubABean extends ABean {
      }
      """
      When build:
      """
      jFactory.spec(SubABean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: HELLO
      """
#      keep no transformer in type and other spec
      When build:
      """
      jFactory.type(Bean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: hello
      """
      And the following spec class:
      """
      public class AnotherBean extends Spec<Bean> {
      }
      """
      When build:
      """
      jFactory.spec(AnotherBean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: hello
      """

    Scenario: define use transformer by global spec
      And the following spec class:
      """
      @Global
      public class ABean extends Spec<Bean> {
      }
      """
      And register:
      """
      jFactory.specFactory(ABean.class).transformer("value", String::toUpperCase);
      """
      And the following spec class:
      """
      public class AnotherBean extends Spec<Bean> {
      }
      """
      When build:
      """
      jFactory.spec(AnotherBean.class).property("value", "hello").create();
      """
      Then the result should:
      """
      value: HELLO
      """

  Rule: sub creation

    Background:
      Given the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
      }
      """

    Scenario: define in type
      And register:
      """
      jFactory.factory(Bean.class).transformer("value", String::toUpperCase);
      """
      When build:
      """
      jFactory.type(BeanWrapper.class).property("bean.value", "hello").create();
      """
      Then the result should:
      """
      bean.value: HELLO
      """

    Scenario: define in spec
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
      }
      """
      And register:
      """
      jFactory.specFactory(ABean.class).transformer("value", String::toUpperCase);
      """
      When build:
      """
      jFactory.type(BeanWrapper.class).property("bean(ABean).value", "hello").create();
      """
      Then the result should:
      """
      bean.value: HELLO
      """

  Rule: sub list element creation

    Background:
      Given the following bean class:
      """
      public class Beans {
        public Bean[] beans;
      }
      """

    Scenario: define in type
      And register:
      """
      jFactory.factory(Bean.class).transformer("value", String::toUpperCase);
      """
      When build:
      """
      jFactory.type(Beans.class).property("beans[0].value", "hello").create();
      """
      Then the result should:
      """
      beans.value[]: [ HELLO ]
      """

    Scenario: define in spec
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
      }
      """
      And register:
      """
      jFactory.specFactory(ABean.class).transformer("value", String::toUpperCase);
      """
      When build:
      """
      jFactory.type(Beans.class).property("beans[0](ABean).value", "hello").create();
      """
      Then the result should:
      """
      beans.value[]: [ HELLO ]
      """
