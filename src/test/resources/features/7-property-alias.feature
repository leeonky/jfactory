Feature: property alias

  Background:
    Given declaration jFactory =
    """
    new JFactory();
    """

  Scenario: define and use alias
    Given the following bean class:
    """
    public class Bean {
      public String value;
    }
    """
    And register:
    """
    jFactory.aliasOf(Bean.class).alias("aliasOfValue", "value");
    """
    When build:
    """
    jFactory.type(Bean.class).property("aliasOfValue", "hello").create();
    """
    Then the result should:
    """
    value: hello
    """

  Scenario: alias of property chain
    Given the following bean class:
    """
    public class Bean {
      public String value;
    }
    """
    Given the following bean class:
    """
    public class BeanWrapper {
      public Bean bean;
    }
    """
    And register:
    """
    jFactory.aliasOf(BeanWrapper.class).alias("aliasOfValue", "bean.value");
    """
    When build:
    """
    jFactory.type(BeanWrapper.class).property("aliasOfValue", "hello").create();
    """
    Then the result should:
    """
    bean.value: hello
    """

  Scenario: alias chain
    Given the following bean class:
    """
    public class Bean {
      public String value;
    }
    """
    Given the following bean class:
    """
    public class BeanWrapper {
      public Bean bean;
    }
    """
    And register:
    """
    jFactory.aliasOf(BeanWrapper.class).alias("aliasOfBean", "bean");
    """
    And register:
    """
    jFactory.aliasOf(Bean.class).alias("aliasOfValue", "value");
    """
    When build:
    """
    jFactory.type(BeanWrapper.class).property("aliasOfBean.aliasOfValue", "hello").create();
    """
    Then the result should:
    """
    bean.value: hello
    """

  Scenario: alias in collection
    Given the following bean class:
    """
    public class Bean {
      public String value;
    }
    """
    Given the following bean class:
    """
    public class BeanWrapper {
      public Bean[] beans;
    }
    """
    And register:
    """
    jFactory.aliasOf(Bean.class).alias("aliasOfValue", "value");
    """
    When build:
    """
    jFactory.type(BeanWrapper.class).property("beans[0].aliasOfValue", "hello").create();
    """
    Then the result should:
    """
    beans: [{value: hello}]
    """

  Scenario: alias of collection property
    Given the following bean class:
    """
    public class Bean {
      public String value;
    }
    """
    Given the following bean class:
    """
    public class BeanWrapper {
      public Bean[] beans;
    }
    """
    And register:
    """
    jFactory.aliasOf(BeanWrapper.class).alias("aliasOfBeans", "beans");
    """
    When build:
    """
    jFactory.type(BeanWrapper.class).property("aliasOfBeans[0].value", "hello").create();
    """
    Then the result should:
    """
    beans: [{value: hello}]
    """
