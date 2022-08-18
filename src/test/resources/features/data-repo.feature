Feature: repo

  Scenario: save bean after create bean
    Given the following bean class:
    """
      public class Bean {
        public String str;
      }
    """
    And create:
    """
      type(Bean.class).property("str", "hello")
    """
    When query:
    """
      type(Bean.class).property("str", "hello")
    """
    Then the result should:
    """
      str= hello
    """

  Scenario: query nothing when property miss
    Given the following bean class:
    """
      public class Bean {
        public String str;
      }
    """
    And create:
    """
      type(Bean.class).property("str", "hello")
    """
    When query:
    """
      type(Bean.class).property("str", "not match")
    """
    Then the result should:
    """
      = null
    """
    When query all:
    """
      type(Bean.class).property("str", "not match")
    """
    Then the result should:
    """
      = []
    """

  Scenario: query nothing when repo is cleared before query
    Given the following bean class:
    """
      public class Bean {
        public String str;
      }
    """
    And create:
    """
      type(Bean.class).property("str", "hello")
    """
    And operate:
    """
      getDataRepository().clear()
    """
    When query:
    """
      type(Bean.class).property("str", "hello")
    """
    Then the result should:
    """
      = null
    """

  Scenario: save and query with property chain
    Given the following bean class:
    """
      public class Bean {
        public String str;
      }
    """
    And the following bean class:
    """
      public class BeanWrapper {
        public Bean bean;
      }
    """
    And create:
    """
      type(BeanWrapper.class).property("bean.str", "hello")
    """
    When query:
    """
      type(BeanWrapper.class).property("bean.str", "hello")
    """
    Then the result should:
    """
      bean.str= hello
    """
    When query:
    """
      type(Bean.class).property("str", "hello")
    """
    Then the result should:
    """
      str= hello
    """
    When query:
    """
      type(BeanWrapper.class).property("bean.str", "not hello")
    """
    Then the result should:
    """
      = null
    """

  Scenario: should use saved been in repo during new creation
    Given the following bean class:
    """
      public class Bean {
        public String str;
      }
    """
    And the following bean class:
    """
      public class BeanWrapper {
        public Bean bean;
      }
    """
    And create:
    """
      type(Bean.class).property("str", "hello")
    """
    When create:
    """
      type(BeanWrapper.class).property("bean.str", "hello")
    """
    Then query all:
    """
      type(Bean.class)
    """
    And the result should:
    """
      ::size= 1
    """

  Scenario: property not match in with different collection
    Given the following bean class:
    """
      public class Bean {
        public String str;
      }
    """
    And the following bean class:
    """
      public class Beans {
        public Bean[] beans;
      }
    """
    And create:
    """
      type(Beans.class).property("beans", new ArrayList<>())
    """
    When create:
    """
      type(Beans.class).property("beans[0].str", "hello")
    """
    And query all:
    """
      type(Beans.class)
    """
    Then the result should:
    """
      ::size= 2
    """

  Scenario: property matches when same collection element, but all collection not equal
    Given the following bean class:
    """
      public class Bean {
        public String str;
      }
    """
    And the following bean class:
    """
      public class Beans {
        public Bean[] beans;
      }
    """
    And the following bean class:
    """
      public class BeansWrapper {
        public Beans beans;
      }
    """
    And create:
    """
      type(BeansWrapper.class)
      .property("beans.beans[0].str", "hello")
      .property("beans.beans[1].str", "world")
    """
    When create:
    """
      type(BeansWrapper.class).property("beans.beans[1].str", "world")
    """
    And query all:
    """
      type(Beans.class)
    """
    Then the result should:
    """
      ::size= 1
    """
