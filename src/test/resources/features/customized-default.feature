Feature: customized default

  Scenario: define customized default value
    Given the following bean class:
    """
      public class Bean {
        public String str;
      }
    """
    When register:
    """
    registerDefaultValueFactory(String.class, new DefaultValueFactory<String>() {
      @Override
        public <T> String create(BeanClass<T> beanType, SubInstance<T> instance) {
          return "hello";
        }
      })
    """
    And create:
    """
      type(Bean.class)
    """
    Then the result should:
    """
      str= hello
    """

  Scenario: skip default value producer
    Given the following bean class:
    """
      public class Bean {
        public String str;
      }
    """
    When register:
    """
      ignoreDefaultValue(propertyWriter -> "str".equals(propertyWriter.getName()))
    """
    And create:
    """
      type(Bean.class)
    """
    Then the result should:
    """
      str= null
    """
