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

  Scenario: recursive alias
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
    jFactory.aliasOf(BeanWrapper.class)
        .alias("beanValue", "bean.value")
        .alias("aliasOfBeanValue", "beanValue");
    """
    When build:
    """
    jFactory.type(BeanWrapper.class).property("aliasOfBeanValue", "hello").create();
    """
    Then the result should:
    """
    bean.value: hello
    """

  Scenario: index arg in alias
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
    jFactory.aliasOf(BeanWrapper.class).alias("beansValue", "beans[$].value");
    """
    When build:
    """
    jFactory.type(BeanWrapper.class).property("beansValue[1]", "hello").create();
    """
    Then the result should:
    """
    beans: [null, {value: hello}]
    """

  Scenario: uses collection alias with collection args
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
    jFactory.aliasOf(BeanWrapper.class).alias("beansValue", "beans[$].value");
    """
    When build:
    """
    jFactory.type(BeanWrapper.class).property("beansValue", java.util.Arrays.asList("hello", "world")).create();
    """
    Then the result should:
    """
    beans.value[]: [hello world]
    """

  Scenario: uses collection alias with empty collection args
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
    jFactory.aliasOf(BeanWrapper.class)
        .alias("beansValue", "beans[$].value")
        .alias("aliasOfBeans", "beans[$]");
    """
    When build:
    """
    jFactory.type(BeanWrapper.class).property("beansValue", new ArrayList<>()).create();
    """
    Then the result should:
    """
    beans: []
    """
    When build:
    """
    jFactory.type(BeanWrapper.class).property("aliasOfBeans", new ArrayList<>()).create();
    """
    Then the result should:
    """
    beans: []
    """

  Scenario: intently creation with alias
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
    And build:
    """
    jFactory.type(BeanWrapper.class).property("aliasOfBean!.value", "hello").create();
    """
    And build:
    """
    jFactory.type(BeanWrapper.class).property("aliasOfBean!.value", "hello").create();
    """
    When build:
    """
    jFactory.type(Bean.class).property("value", "hello").queryAll();
    """
    Then the result should:
    """
    value[]: [hello hello]
    """

  Scenario: define alias in spec class
    Given the following bean class:
    """
    public class Bean {
      public String value;
    }
    """
    And the following spec class:
    """
    @PropertyAliases(
            @PropertyAlias(alias = "aliasOfValue", property = "value")
    )
    public class ABean extends Spec<Bean> {
    }
    """
    When build:
    """
    jFactory.spec(ABean.class).property("aliasOfValue", "hello").create();
    """
    Then the result should:
    """
    value: hello
    """
    When build:
    """
    jFactory.type(Bean.class).property("aliasOfValue", "hello").create();
    """
    Then should raise error:
    """
    : {
      class.simpleName: NoSuchPropertyException
      message: 'No available property: Bean.aliasOfValue'
    }
    """

  Scenario: use type alias when missed alias on spec
    Given the following bean class:
    """
    public class Bean {
      public String value;
    }
    """
    And the following spec class:
    """
    public class ABean extends Spec<Bean> {
    }
    """
    And register:
    """
    jFactory.aliasOf(Bean.class).alias("aliasOfValue", "value");
    """
    When build:
    """
    jFactory.spec(ABean.class).property("aliasOfValue", "hello").create();
    """
    Then the result should:
    """
    value= hello
    """

  Scenario: alias in super spec
    Given the following bean class:
    """
    public class Bean {
      public String value;
    }
    """
    And the following spec class:
    """
    @PropertyAliases(
            @PropertyAlias(alias = "aliasOfValue", property = "value")
    )
    public class ABean extends Spec<Bean> {
    }
    """
    And the following spec class:
    """
    public class SubSpec extends ABean {
    }
    """
    When build:
    """
    jFactory.spec(SubSpec.class).property("aliasOfValue", "hello").create();
    """
    Then the result should:
    """
    value= hello
    """

  Scenario: alias on global spec
    Given the following bean class:
    """
    public class Bean {
      public String value;
    }
    """
    And the following spec class:
    """
    @Global
    @PropertyAliases(
            @PropertyAlias(alias = "aliasOfValue", property = "value")
    )
    public class ABean extends Spec<Bean> {
    }
    """
    When build:
    """
    jFactory.type(Bean.class).property("aliasOfValue", "hello").create();
    """
    Then the result should:
    """
    value= hello
    """

  Scenario: use global alias when missed alias on spec
    Given the following bean class:
    """
    public class Bean {
      public String value;
    }
    """
    And the following spec class:
    """
    @Global
    @PropertyAliases(
            @PropertyAlias(alias = "aliasOfValue", property = "value")
    )
    public class GlobalBean extends Spec<Bean> {
    }
    """
    And the following spec class:
    """
    public class ABean extends Spec<Bean> {
    }
    """
    When build:
    """
    jFactory.spec(ABean.class).property("aliasOfValue", "hello").create();
    """
    Then the result should:
    """
    value= hello
    """

  Scenario: sub spec alias override super spec alias
    Given the following bean class:
    """
    public class Bean {
      public String value1, value2;
    }
    """
    And the following spec class:
    """
    @PropertyAliases(
            @PropertyAlias(alias = "aliasOfValue", property = "value1")
    )
    public class SuperBean extends Spec<Bean> {
    }
    """
    And the following spec class:
    """
    @PropertyAliases(
            @PropertyAlias(alias = "aliasOfValue", property = "value2")
    )
    public class SubBean extends SuperBean {
    }
    """

    When build:
    """
    jFactory.spec(SubBean.class).property("aliasOfValue", "hello").create();
    """
    Then the result should:
    """
    value2= hello
    """
