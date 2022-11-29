Feature: transformer

  Rule: single creation
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
