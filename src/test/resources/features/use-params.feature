Feature: create with params

  Scenario: create bean with params
    Given the following bean class:
    """
      public class Bean {
        public String str1, str2;
      }
    """
    And register:
    """
      factory(Bean.class).spec(instance -> instance.spec()
        .property("str1").value((Object) instance.param("p1"))
        .property("str2").value((Object) instance.param("p2")))
    """
    When create:
    """
      type(Bean.class).arg("p1", "foo")
    """
    Then the result should:
    """
      str1= foo
    """
    When create:
    """
      type(Bean.class).args(new HashMap<String, String>() {{
        put("p1", "hello");
        put("p2", "world");
      }})
    """
    Then the result should:
    """
      = {
        str1= hello
        str2= world
      }
    """

  Scenario: support default params
    Given the following bean class:
    """
      public class Bean {
        public String str1, str2;
      }
    """
    And register:
    """
      factory(Bean.class).spec(instance -> instance.spec()
        .property("str1").value((Object) instance.param("p1", "default1"))
        .property("str2").value((Object) instance.param("p2", "default2")))
    """
    When create:
    """
      type(Bean.class)
    """
    Then the result should:
    """
      = {
        str1= default1
        str2= default2
      }
    """

  Scenario: support use arg method to pass args
    Given the following bean class:
    """
      public class Bean {
        public String str1, str2;
      }
    """
    And register:
    """
      factory(Bean.class).spec(instance -> instance.spec()
        .property("str1").value((Object) instance.param("p1"))
        .property("str2").value((Object) instance.param("p2")))
    """
    When create:
    """
      type(Bean.class).args(arg("p1", "hello").arg("p2", "world"))
    """
    Then the result should:
    """
      = {
        str1= hello
        str2= world
      }
    """

  Scenario: support nested args
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
    And register:
    """
      factory(Bean.class).spec(instance -> instance.spec()
        .property("str").value((Object) instance.param("p")))
    """
    And register:
    """
      factory(BeanWrapper.class).spec(instance -> instance.spec()
        .property("bean").byFactory())
    """
    When create:
    """
      type(BeanWrapper.class).args("bean", arg("p", "hello"))
    """
    Then the result should:
    """
      bean.str= hello
    """

  Scenario: support nested args in deep levels
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
    And the following bean class:
    """
      public class BeanWrapperWrapper {
        public BeanWrapper beanWrapper;
      }
    """
    And register:
    """
      factory(Bean.class).spec(instance -> instance.spec()
        .property("str").value((Object) instance.param("p")))
    """
    And register:
    """
      factory(BeanWrapper.class).spec(instance -> instance.spec()
        .property("bean").byFactory())
    """
    And register:
    """
      factory(BeanWrapperWrapper.class).spec(instance -> instance.spec()
        .property("beanWrapper").byFactory())
    """
    When create:
    """
      type(BeanWrapperWrapper.class).args("beanWrapper.bean", arg("p", "hello"))
    """
    Then the result should:
    """
      beanWrapper.bean.str= hello
    """

  Scenario: use args in spec class
    Given the following bean class:
    """
      public class Bean {
        public String str;
      }
    """
    And the following spec class:
    """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("str").value((Object)param("p"));
        }
      }
    """
    When create:
    """
      spec(ABean.class).arg("p", "hello")
    """
    Then the result should:
    """
      str= hello
    """

  Scenario: fetch nested arg in spec
    Given the following bean class:
    """
      public class Bean {
        public String str;
        public Bean setStr(String s) {
          this.str = s;
          return this;
        }
      }
    """
    And the following bean class:
    """
      public class BeanWrapper {
        public Bean bean;
        public BeanWrapper setBean(Bean b) {
          this.bean = b;
          return this;
        }
      }
    """
    And the following bean class:
    """
      public class BeanWrapperWrapper {
        public BeanWrapper beanWrapper;
      }
    """
    And register:
    """
      factory(BeanWrapperWrapper.class).spec(instance -> instance.spec()
        .property("beanWrapper").value(new BeanWrapper().setBean(new Bean()
          .setStr(instance.params("beanWrapper").params("bean").param("p")))))
    """
    When create:
    """
      type(BeanWrapperWrapper.class).args("beanWrapper.bean", arg("p", "hello"))
    """
    Then the result should:
    """
      beanWrapper.bean.str= hello
    """

  Scenario: fetch all params in spec
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
    And register:
    """
      factory(Bean.class).spec(instance -> instance.spec()
        .property("str").value((Object) instance.param("p")))
    """
    And register:
    """
      factory(BeanWrapper.class).spec(instance -> instance.spec()
        .property("bean").byFactory(builder ->builder.args(instance.params())))
    """
    When create:
    """
      type(BeanWrapper.class).arg("p", "hello")
    """
    Then the result should:
    """
      bean.str= hello
    """
