Feature: define link

  Background:
    Given declaration jFactory =
    """
    new JFactory();
    """

  Rule: top level link

    Scenario: property link property
      Given the following bean class:
      """
      public class Bean {
        public String str1, str2;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
          .link("str1", "str2"));
      """
      When build:
      """
      jFactory.type(Bean.class).create();
      """
      Then the result should:
      """
      <<str1, str2>>= /str.*/ and str1= .str2
      """

    Scenario: connect link
      Given the following bean class:
      """
      public class Bean {
        public String str1, str2, str3, str4;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
          .link("str1", "str2")
          .link("str3", "str4")
          .link("str2", "str3"));
      """
      When build:
      """
      jFactory.type(Bean.class).create();
      """
      Then the result should:
      """
      <<str1, str2, str3, str4>>= /str.*/ and str1= .str2 and str2= .str3 and str3= .str4
      """

    Scenario: link object property - bug
      Given the following bean class:
      """
      public class Bean {
      }
      """
      Given the following bean class:
      """
      public class Beans {
        public Bean bean1, bean2;
      }
      """
      And register:
      """
      jFactory.factory(Beans.class).spec(instance -> instance.spec()
          .property("bean1").byFactory()
          .link("bean1", "bean2"));
      """
      When build:
      """
      jFactory.type(Beans.class).create();
      """
      Then the result should:
      """
      bean1= .bean2 and bean1: {...}
      """

    Scenario: link object property
      Given the following bean class:
      """
      public class Bean {
      }
      """
      Given the following bean class:
      """
      public class Beans {
        public Bean bean1, bean2;
      }
      """
      And register:
      """
      jFactory.factory(Beans.class).spec(instance -> instance.spec()
          .property("bean1").byFactory()
          .link("bean1", "bean2"));
      """
      When build:
      """
      jFactory.type(Beans.class).property("bean2", new Bean()).create();
      """
#      Then the result should:
#      """
#      : {
#        bean1= .bean2
#        bean1: {...}
#      }
#      """

  Rule: link in collection

    Scenario: link element property and input sub property
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given the following bean class:
      """
      public class Beans {
        public Bean[] beans;
      }
      """
      And register:
      """
      jFactory.factory(Beans.class).spec(instance -> instance.spec()
          .property("beans[0]").byFactory()
          .property("beans[1]").byFactory()
          .property("beans[2]").byFactory()
          .link("beans[0].value", "beans[1].value", "beans[2].value"));
      """
      When build:
      """
      jFactory.type(Beans.class).property("beans[2].value", "hello").create();
      """
      Then the result should:
      """
      <<beans[0], beans[1], beans[2]>>.value= hello
      """

    Scenario: link element and input sub property
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given the following bean class:
      """
      public class Beans {
        public Bean[] beans;
        public Bean bean;
      }
      """
      And register:
      """
      jFactory.factory(Beans.class).spec(instance -> instance.spec()
          .property("beans[0]").byFactory()
          .property("beans[1]").byFactory()
          .link("beans[0]", "beans[1]", "bean"));
      """
      When build:
      """
      jFactory.type(Beans.class).property("bean.value", "hello").create();
      """
#      TODO BUG not implement
#      Then the result should:
#      """
#      <<beans[0], beans[1], bean>>.value= hello
#      """

    Scenario: link element and input object
      Given the following bean class:
      """
      public class Bean {
        public String value;
        public Bean setValue(String str) {
          this.value = str;
          return this;
        }
      }
      """
      Given the following bean class:
      """
      public class Beans {
        public Bean[] beans;
        public Bean bean;
      }
      """
      And register:
      """
      jFactory.factory(Beans.class).spec(instance -> instance.spec()
          .link("beans[0]", "beans[1]", "bean"));
      """
      When build:
      """
      jFactory.type(Beans.class).property("bean", new Bean().setValue("hello")).create();
      """
      Then the result should:
      """
      <<beans[0], beans[1], bean>>.value= hello
      """

    Scenario: link element property and input property
      Given the following bean class:
      """
      public class Bean {
        public String value;
        public Bean setValue(String str) {
          this.value = str;
          return this;
        }
      }
      """
      Given the following bean class:
      """
      public class Beans {
        public Bean[] beans;
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(Beans.class).spec(instance -> instance.spec()
          .property("beans[0]").byFactory()
          .property("beans[1]").byFactory()
          .link("beans[0].value", "beans[1].value", "value"));
      """
      When build:
      """
      jFactory.type(Beans.class).property("value", "hello").create();
      """
      Then the result should:
      """
      <<beans[0].value, beans[1].value, value>>= hello
      """

    Scenario: support negative index in link
      Given the following bean class:
      """
      public class Bean {
        public String[] values;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
          .link("values[0]", "values[-1]"));
      """
      When build:
      """
      jFactory.type(Bean.class).property("values[1]", "hello").property("values[2]", "world").create();
      """
      Then the result should:
      """
      values= [world hello world]
      """

  Rule: nested link

    Scenario: connect link in sub object link
      Given the following bean class:
      """
      public class Bean {
        public String str1, str2;
      }
      """
      Given the following bean class:
      """
      public class BeanWrapper {
        public String value;
        public Bean bean;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
          .property("str1").value("hello")
          .link("str1", "str2"));
      """
      And register:
      """
      jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
          .property("bean").byFactory()
          .link("value", "bean.str2"));
      """
      When build:
      """
      jFactory.type(BeanWrapper.class).create();
      """
      Then the result should:
      """
      <<bean.str1, bean.str2, value>>= hello
      """

    Scenario: link in collection element
      Given the following bean class:
      """
      public class Bean {
        public String str1, str2;
      }
      """
      Given the following bean class:
      """
      public class Beans {
        public Bean[] beans;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
          .link("str1", "str2"));
      """
      And register:
      """
      jFactory.factory(Beans.class).spec(instance -> instance.spec()
          .property("beans[0]").byFactory());
      """
      When build:
      """
      jFactory.type(Beans.class).create();
      """
      Then the result should:
      """
      <<beans[0].str1, beans[0].str2>>= /str.*/ and beans[0].str1= .beans[0].str2
      """

    Scenario: link was replaced by parent link
      Given the following bean class:
      """
      public class Bean {
        public String value;
        public Bean setValue(String v) {
          this.value = v;
          return this;
        }
      }
      """

      Given the following bean class:
      """
      public class BeanWrapper {
        public Bean bean1, bean2;
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
          .property("value").value("hello"));
      """
      And register:
      """
      jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
        .link("bean1.value", "value")
        .link("bean1", "bean2"));
      """
      When build:
      """
      jFactory.type(BeanWrapper.class).property("bean2", new Bean().setValue("foo")).create();
      """
      Then the result should:
      """
      <<bean1.value, bean2.value, value>>= foo
      """

  Rule: link priority

    Scenario: use property value in link and keep readonly value
      Given the following bean class:
      """
      public class Bean {
        public String value;
        public Bean setValue(String v) {
          this.value = v;
          return this;
        }
      }
      """

      Given the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
        .link("bean.value", "value"));
      """
      When build:
      """
      jFactory.type(BeanWrapper.class)
          .property("bean", new Bean().setValue("foo"))
          .property("value", "bar").create();
      """
      Then the result should:
      """
      : {
        bean.value: foo
        value: bar
      }
      """

    Scenario: use read only value in link
      Given the following bean class:
      """
      public class Bean {
        public String value;
        public Bean setValue(String v) {
          this.value = v;
          return this;
        }
      }
      """

      Given the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
        public String value, value2;
      }
      """
      And register:
      """
      jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
        .property("value").dependsOn("value2", o -> o)
        .link("bean.value", "value"));
      """
      When build:
      """
      jFactory.type(BeanWrapper.class)
          .property("bean", new Bean().setValue("hello")).create();
      """
      Then the result should:
      """
      <<bean.value, value>>= hello
      """

    Scenario: use dependency value
      Given the following bean class:
      """
      public class Bean {
        public String str, str1, str2, str3, str4;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
        .property("str2").dependsOn("str", o->o)
        .property("str3").value("default")
        .link("str1", "str2", "str3", "str4"));
      """
      When build:
      """
      jFactory.type(Bean.class)
          .property("str1", "hello").create();
      """
      Then the result should:
      """
      <<str1, str2, str3, str4>>= hello
      """

    Scenario: use suppose value
      Given the following bean class:
      """
      public class Bean {
        public String str1, str2;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
        .property("str1").value("hello")
        .link("str1", "str2"));
      """
      When build:
      """
      jFactory.type(Bean.class).create();
      """
      Then the result should:
      """
      <<str1, str2>>= hello
      """

  Rule: link parent object but sub property is not default

    Scenario: link object and input sub property
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given the following bean class:
      """
      public class Beans {
        public Bean bean1, bean2;
      }
      """
      And register:
      """
      jFactory.factory(Beans.class).spec(instance -> instance.spec()
        .property("bean1").byFactory()
        .property("bean2").byFactory()
        .link("bean1", "bean2")
        );
      """
      When build:
      """
      jFactory.type(Beans.class).property("bean2.value", "hello").create();
      """
      Then the result should:
      """
      : {
        <<bean1.value bean2.value>>= hello
        .bean1= .bean2
      }
      """

  Rule: link to read only

    Scenario: link with read only value
      Given the following bean class:
      """
      public class Bean {
        public String value;
        public Bean setValue(String v) {
          this.value = v;
          return this;
        }
      }
      """

      Given the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
        .link("bean.value", "value"));
      """
      When build:
      """
      jFactory.type(BeanWrapper.class)
          .property("bean", new Bean().setValue("hello")).create();
      """
      Then the result should:
      """
      <<bean.value, value>>= hello
      """

    Scenario: use default value(null) when parent object is null
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
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
        .link("bean.value", "value"));
      """
      When build:
      """
      jFactory.type(BeanWrapper.class).create();
      """
      Then the result should:
      """
      <<bean, value>>= null
      """

    Scenario: link with read only value from suppose object
      Given the following bean class:
      """
      public class Bean {
        public String value;
        public Bean setValue(String v) {
          this.value = v;
          return this;
        }
      }
      """

      Given the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
        .property("bean").value(new Bean().setValue("hello"))
        .link("bean.value", "value"));
      """
      When build:
      """
      jFactory.type(BeanWrapper.class).create();
      """
      Then the result should:
      """
      <<bean.value, value>>= hello
      """

    Scenario: link with read only value from another link
      Given the following bean class:
      """
      public class Bean {
        public String value;
        public Bean setValue(String v) {
          this.value = v;
          return this;
        }
      }
      """

      Given the following bean class:
      """
      public class BeanWrapper {
        public Bean bean, another;
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
        .link("bean.value", "value")
        .link("bean", "another"));
      """
      When build:
      """
      jFactory.type(BeanWrapper.class).property("another", new Bean().setValue("hello")).create();
      """
      Then the result should:
      """
      <<bean.value, value>>= hello
      """
