Feature: input property

  Rule: input multi properties for sub object
    Scenario: support multi properties in nested property creation and query
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2;
      }
      """
      And the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
      }
      """
      And the following bean class:
      """
      public class BeanWrapperWrapper {
        public BeanWrapper beanWrapper;
      }
      """
      When build:
      """
      jFactory.type(BeanWrapperWrapper.class)
        .property("beanWrapper.bean.value1", "hello")
        .property("beanWrapper.bean.value2", "world")
        .create();
      """
      Then the result should:
      """
      beanWrapper.bean= {
        value1= hello
        value2= world
      }
      """
      And operate:
      """
      jFactory.type(BeanWrapperWrapper.class)
        .property("beanWrapper.bean.value1", "hello")
        .property("beanWrapper.bean.value2", "world")
        .create();
      """
      When build:
      """
      jFactory.type(BeanWrapperWrapper.class)
        .property("beanWrapper.bean.value1", "hello")
        .property("beanWrapper.bean.value2", "world")
        .query();
      """
      Then the result should:
      """
      beanWrapper.bean= {
        value1= hello
        value2= world
      }
      """
