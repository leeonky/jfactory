Feature: define dependency

  Background:
    Given declaration jFactory =
    """
    new JFactory();
    """

  Rule: top level dependency

    Scenario: one property depends on another property in same bean
      Given the following bean class:
      """
      public class Bean {
        public int intValue;
        public String stringValue;
      }
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("stringValue").dependsOn("intValue", Object::toString);
        }
      }
      """
      When build:
      """
      jFactory.spec(ABean.class).create();
      """
      Then the result should:
      """
      stringValue= .intValue.toString
      """

    Scenario: specify a property value which has a dependency
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2;
      }
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value1").dependsOn("value2", o -> o);
        }
      }
      """
      When build:
      """
      jFactory.spec(ABean.class).property("value2", 100).create();
      """
      Then the result should:
      """
      <<value1 value2>>= '100'
      """

    Scenario: one property depends on multi properties
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2, value3;
      }
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value3").dependsOn(Arrays.asList("value1", "value2"), args -> args[0].toString() + args[1]);
        }
      }
      """
      When build:
      """
      jFactory.spec(ABean.class).create();
      """
      Then the result should:
      """
      value3= .value1 + .value2
      """
      When build:
      """
      jFactory.spec(ABean.class)
        .property("value1", "Hello")
        .property("value2", "World")
        .create();
      """
      Then the result should:
      """
      value3= HelloWorld
      """

    Scenario: dependency chain
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2, value3;
      }
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value1").dependsOn("value2", o -> o);
          property("value2").dependsOn("value3", o -> o);
        }
      }
      """
      When build:
      """
      jFactory.spec(ABean.class).property("value3", "hello").create();
      """
      Then the result should:
      """
      <<value1 value2 value3>>= hello
      """

    Scenario: dependency spec should override value spec
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2;
      }
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value1").value(null);
          property("value1").dependsOn("value2", o -> o);
        }
      }
      """
      When build:
      """
      jFactory.spec(ABean.class).property("value2", "hello").create();
      """
      Then the result should:
      """
      <<value1 value2>>= hello
      """

    Scenario: input property should override dependency spec
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2;
      }
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value1").dependsOn("value2", o -> o);
        }
      }
      """
      When build:
      """
      jFactory.spec(ABean.class)
        .property("value1", "hello")
        .property("value2", "world")
      .create();
      """
      Then the result should:
      """
      = {
        value1= hello
        value2= world
      }
      """

  Rule: collection dependency

    Scenario: dependency between collection element
      Given the following bean class:
      """
      public class Bean {
        public String strings[];
      }
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("strings[2]").dependsOn("strings[1]", obj -> obj);
          property("strings[1]").dependsOn("strings[0]", obj -> obj);
        }
      }
      """
      When build:
      """
      jFactory.spec(ABean.class)
        .property("strings[0]", "hello")
      .create();
      """
      Then the result should:
      """
      strings= [hello hello hello]
      """

    Scenario: dependency between collection element and bean property
      Given the following bean class:
      """
      public class Bean {
        public String strings[], value;
      }
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("strings[2]").dependsOn("strings[1]", obj -> obj);
          property("strings[1]").dependsOn("strings[0]", obj -> obj);
          property("strings[0]").dependsOn("value", obj -> obj);
        }
      }
      """
      When build:
      """
      jFactory.spec(ABean.class)
        .property("value", "hello")
      .create();
      """
      Then the result should:
      """
      = {
        value= hello
        strings= [hello hello hello]
      }
      """

    Scenario: dependency spec should override value spec
      Given the following bean class:
      """
      public class Bean {
        public String strings[];
      }
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("strings[1]").byFactory();
          property("strings[1]").dependsOn("strings[0]", obj -> obj);
        }
      }
      """
      When build:
      """
      jFactory.spec(ABean.class)
        .property("strings[0]", "hello")
      .create();
      """
      Then the result should:
      """
      strings= [hello hello]
      """

    Scenario: input property should override dependency spec
      Given the following bean class:
      """
      public class Bean {
        public String strings[];
      }
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("strings[1]").dependsOn("strings[0]", obj -> obj);
        }
      }
      """
      When build:
      """
      jFactory.spec(ABean.class)
        .property("strings[0]", "hello")
        .property("strings[1]", "world")
      .create();
      """
      Then the result should:
      """
      strings= [hello world]
      """

  Rule: sub object property dependency

    Scenario: dependency on different level property
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
        .property("bean").byFactory()
        .property("bean.value").dependsOn("value", o -> o));
      """
      When build:
      """
      jFactory.type(BeanWrapper.class).property("value", "hello").create();
      """
      Then the result should:
      """
      <<bean.value, value>>= hello
      """
      Given register:
      """
      jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
        .property("value").dependsOn("bean.value", o -> o));
      """
      When build:
      """
      jFactory.type(BeanWrapper.class).property("bean.value", "hello").create();
      """
      Then the result should:
      """
      <<bean.value, value>>= hello
      """

    Scenario: input property should override dependency
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
        .property("bean").byFactory()
        .property("bean.value").dependsOn("value", o -> o));
      """
      When build:
      """
      jFactory.type(BeanWrapper.class)
        .property("value", "hello")
        .property("bean.value", "world")
        .create();
      """
      Then the result should:
      """
      = {
        value= hello
        bean.value= world
      }
      """

    Scenario: ignore dependency when parent object was replaced by input property
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
        .property("bean").byFactory()
        .property("bean.value").dependsOn("value", o -> o));
      """
      When build:
      """
      jFactory.type(BeanWrapper.class)
        .property("value", "hello")
        .property("bean", new Bean() {{
          this.value = "world";
        }})
        .create();
      """
      Then the result should:
      """
      = {
        value= hello
        bean.value= world
      }
      """

    Scenario: ignore dependency when parent object was replaced by depenedency
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
        public Bean another;
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
        .property("bean").dependsOn("another", o -> o)
        .property("bean.value").dependsOn("value", o -> o));
      """
      When build:
      """
      jFactory.type(BeanWrapper.class)
        .property("value", "hello")
        .property("another", new Bean() {{
          this.value = "world";
        }})
        .create();
      """
      Then the result should:
      """
      = {
        value= hello
        bean= {
          value= world
        }
        another= {
          value= world
        }
      }
      """

  Rule: sub collection element dependency

    Background:
      Given the following bean class:
      """
      public class Bean {
        public String stringValue;
      }
      """
      And the following bean class:
      """
      public class BeanArray {
        public Bean[] beans;
      }
      """
      And register:
      """
      jFactory.factory(BeanArray.class).spec(instance -> instance.spec()
          .property("beans[0]").byFactory()
          .property("beans[1]").byFactory()
          .property("beans[0].stringValue").dependsOn("beans[1].stringValue", obj -> obj));
      """

    Scenario: depends on default value between sub collection element
      When build:
      """
      jFactory.type(BeanArray.class).create();
      """
      Then the result should:
      """
      beans[0].stringValue = .beans[1].stringValue
      """

    Scenario: depends on input value between sub collection element
      When build:
      """
      jFactory.type(BeanArray.class).property("beans[1].stringValue", "hello").create();
      """
      Then the result should:
      """
      beans<<[0], [1]>>.stringValue= hello
      """

    Scenario: ignore dependency when input property override dependency
      When build:
      """
      jFactory.type(BeanArray.class).property("beans[0].stringValue", "hello").create();
      """
      Then the result should:
      """
      beans: [{
        stringValue= hello
      }, {
        stringValue= /stringValue#.*/
      }]
      """

    Scenario: ignore dependency when parent object was replaced by input property in collection
      Given the following bean class:
      """
      public class BeanArrays {
        public BeanArray[] beanArrays;
      }
      """
      When build:
      """
      jFactory.type(BeanArray.class).property("beans[0]", null).create();
      """
      Then the result should:
      """
      beans: [null, {
        stringValue= /stringValue#.*/
      }]
      """

  Rule: sub level dependency

    Scenario: sub dependency in object
      Given the following bean class:
      """
      public class Strings {
        public String str1, str2;
      }
      """
      And the following bean class:
      """
      public class Bean {
        public String value;
        public Strings strings;
      }
      """
      And register:
      """
      jFactory.factory(Strings.class).spec(instance -> instance.spec()
          .property("str1").dependsOn("str2", obj -> obj));
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
          .property("value").dependsOn("strings.str1", obj -> obj));
      """
      When build:
      """
      jFactory.type(Bean.class).property("strings.str2", "hello").create();
      """
      Then the result should:
      """
      <<value, strings.str1, strings.str2>>= hello
      """

    Scenario: sub dependency in collection element
      Given the following bean class:
      """
      public class Bean {
        public String str1, str2;
      }
      """
      And the following bean class:
      """
      public class BeanArray {
        public Bean[] beans;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
          .property("str1").dependsOn("str2", obj -> obj));
      """
      And register:
      """
      jFactory.factory(BeanArray.class).spec(instance -> instance.spec()
          .property("beans[0]").byFactory());
      """
      When build:
      """
      jFactory.type(BeanArray.class).property("beans[0].str2", "hello").create();
      """
      Then the result should:
      """
      beans[0]<<str1, str2>>= hello
      """

  Rule: ignore dependency

    Scenario: ignore dependency when no parent object factory
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given the following bean class:
      """
      public class Beans {
        public Bean bean;
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(Beans.class).spec(instance -> instance.spec()
          .property("bean.value").dependsOn("value", obj -> obj));
      """
      When build:
      """
      jFactory.type(Beans.class).property("value", "hello").create();
      """
      Then the result should:
      """
      : {
        bean= null
        value= hello
      }
      """

    Scenario: ignore dependency when no parent object factory in collection
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
          .property("beans[0].value").dependsOn("beans[1].value", obj -> obj));
      """
      When build:
      """
      jFactory.type(Beans.class).property("beans[1].value", "hello").create();
      """
      Then the result should:
      """
      beans: [
        null
        {
          value= hello
        }
      ]
      """

    Scenario: ignore dependency when parent object was replaced by input property
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given the following bean class:
      """
      public class Beans {
        public Bean bean;
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(Beans.class).spec(instance -> instance.spec()
          .property("bean.value").dependsOn("value", obj -> obj));
      """
      When build:
      """
      jFactory.type(Beans.class).property("bean", null).property("value", "hello").create();
      """
      Then the result should:
      """
      : {
        bean= null
        value= hello
      }
      """

  Rule: dependency value

    Scenario: depends on value from input object
      Given the following bean class:
      """
      public class Bean {
        private String value;
        public Bean setValue(String str) {
          this.value = str;
          return this;
        }

        public String getValue() {
          return value;
        }
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
          .property("bean1").dependsOn("bean2", obj -> obj));
      """
      When build:
      """
      jFactory.type(Beans.class).property("bean2", new Bean().setValue("hello")).create();
      """
      Then the result should:
      """
      <<bean1 bean2>>.value= hello
      """

    Scenario: depends on value from input object property
      Given the following bean class:
      """
      public class Bean {
        private String value;
        public Bean setValue(String str) {
          this.value = str;
          return this;
        }

        public String getValue() {
          return value;
        }
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
          .property("bean1.value").dependsOn("bean2.value", obj -> obj));
      """
      When build:
      """
      jFactory.type(Beans.class).property("bean2", new Bean().setValue("hello")).create();
      """
      Then the result should:
      """
      <<bean1 bean2>>.value= hello
      """

    Scenario: depends on value from created object property in customized constructor
      Given the following bean class:
      """
      public class Bean {
        private String value;
        public Bean setValue(String str) {
          this.value = str;
          return this;
        }

        public String getValue() {
          return value;
        }
      }
      """
      Given the following bean class:
      """
      public class Beans {
        public Bean bean1, bean2;
        public Beans setBean2(Bean bean) {
          this.bean2 = bean;
          return this;
        }
      }
      """
      And register:
      """
      jFactory.factory(Beans.class)
          .constructor(instance -> new Beans().setBean2(new Bean().setValue("hello")))
          .spec(instance -> instance.spec()
          .property("bean1").byFactory()
          .property("bean1.value").dependsOn("bean2.value", obj -> obj));
      """
      When build:
      """
      jFactory.type(Beans.class).create();
      """
      Then the result should:
      """
      <<bean1 bean2>>.value= hello
      """

    Scenario: depends on value from created object property in customized constructor in collection
      Given the following bean class:
      """
      public class Bean {
        private String value;
        public Bean setValue(String str) {
          this.value = str;
          return this;
        }

        public String getValue() {
          return value;
        }
      }
      """
      Given the following bean class:
      """
      public class Beans {
        public Bean[] beans1, beans2;
      }
      """
      And register:
      """
      jFactory.factory(Beans.class)
          .constructor(instance -> {
            Beans beans = new Beans();
            beans.beans2 = new Bean[]{new Bean().setValue("hello")};
            return beans;
          })
          .spec(instance -> instance.spec()
          .property("beans1[0]").byFactory()
          .property("beans1[0].value").dependsOn("beans2[0].value", obj -> obj));
      """
      When build:
      """
      jFactory.type(Beans.class).create();
      """
      Then the result should:
      """
      <<beans1, beans2>>[0].value= hello
      """

    Scenario: should use default value(null) when depended value parent object is null

      Given the following bean class:
      """
      public class Bean {
        private String value;
        public Bean setValue(String str) {
          this.value = str;
          return this;
        }

        public String getValue() {
          return value;
        }
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
          .property("bean1.value").dependsOn("bean2.value", obj -> obj));
      """
      When build:
      """
      jFactory.type(Beans.class).create();
      """
      Then the result should:
      """
      : {
        bean1.value= null
        bean2= null
      }
      """
