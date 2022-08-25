Feature: use spec

  Rule: spec class

    Scenario: define class - define spec and trait in class
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2;
      }
      """
      Given the following spec class:
      """
      @Global
      public class ABean extends Spec<Bean> {

        @Override
        public void main() {
          property("value2").value("world");
        }

        @Trait
        public void hello() {
          property("value1").value("hello");
        }
      }
      """
      When build:
      """
      jFactory.spec(ABean.class).traits("hello").create();
      """
      Then the result should:
      """
      = {
        value1= hello
        value2= world
      }
      """
      When build:
      """
      jFactory.createAs(ABean.class, spec -> spec.hello());
      """
      Then the result should:
      """
      = {
        value1= hello
        value2= world
      }
      """
      When build:
      """
      jFactory.createAs("hello", "ABean");
      """
      Then the result should:
      """
      = {
        value1= hello
        value2= world
      }
      """

    Scenario: inherit - should call type base spec
      Given the following bean class:
      """
      public class Bean {
        private String content;
        public String stringValue;
        public Bean() {
          this.content = "spec create";
        }
        public Bean(String content) {
          this.content = content;
        }
        public String getContent() {
          return content;
        }
      }
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).constructor(instance -> new Bean("base create"))
        .spec(instance -> instance.spec().property("stringValue").value("base property"));
      """
      When build:
      """
      jFactory.spec(ABean.class).create();
      """
      Then the result should:
      """
      = {
        content= 'base create'
        stringValue= 'base property'
      }
      """

  Rule: override

    Scenario: naming spec(trait) override spec in type
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given register:
      """
      factory(Bean.class)
        .spec(instance-> instance.spec().property("value").value("type spec"))
        .spec("hello", instance-> instance.spec().property("value").value("hello"))
      """
      When build:
      """
      jFactory.type(Bean.class).traits("hello").create();
      """
      Then the result should:
      """
      value: hello
      """

    Scenario: trait in spec class override spec in type
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given register:
      """
      factory(Bean.class)
        .spec(instance-> instance.spec().property("value").value("type spec"))
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Trait
        public void hello() {
          property("value").value("hello");
        }
      }
      """
      When build:
      """
      jFactory.spec("hello", "ABean").create();
      """
      Then the result should:
      """
      value: hello
      """

    Scenario: trait in spec class override spec in spec class
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value").value("type spec");
        }

        @Trait
        public void hello() {
          property("value").value("hello");
        }
      }
      """
      When build:
      """
      jFactory.spec("hello", "ABean").create();
      """
      Then the result should:
      """
      value: hello
      """

    Scenario: trait in spec class override spec in spec instance
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value").value("type spec");
        }

        @Trait
        public ABean hello() {
          property("value").value("hello");
          return this;
        }
      }
      """
      When build:
      """
      jFactory.createAs(ABean.class, spec -> spec.hello());
      """
      Then the result should:
      """
      value: hello
      """

    Scenario: trait in spec class override spec in global spec class
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given the following spec class:
      """
      @Global
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value").value("type spec");
        }

        @Trait
        public void hello() {
          property("value").value("hello");
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).traits("hello").create();
      """
      Then the result should:
      """
      value: hello
      """

    Scenario: avoid duplicated execute base spec
      Given the following bean class:
      """
      public class Bean {
        public int value;
      }
      """
      Given the following spec class:
      """
      @Global
      public class ABean extends Spec<Bean> {
        private static int i = 0;

        @Override
        public void main() {
          property("value").value(i++);
        }
      }
      """
      When build:
      """
      jFactory.spec(ABean.class).create();
      """
      Then the result should:
      """
      value: 0
      """

  Rule: invalid spec/trait

    Scenario: raise error when spec not exist
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given the following spec class:
      """
      @Global
      public class ABean extends Spec<Bean> {
      }
      """
      When build:
      """
      jFactory.createAs("NotExist");
      """
      Then should raise error:
      """
      message= 'Spec `NotExist` not exist'
      """

    Scenario: raise error when trait not exist
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).traits("not-exist").create();
      """
      Then should raise error:
      """
      message= 'Trait `not-exist` not exist'
      """

    Scenario: do not allow generic base spec class
      Given the following spec class:
      """
      public class Spec2<T> extends Spec<T> {
      }
      """
      And the following spec class:
      """
      public class InvalidGenericArgSpec extends Spec2<String> {
      }
      """
      When build:
      """
      jFactory.createAs(InvalidGenericArgSpec.class);
      """
      Then should raise error:
      """
      message= 'Cannot guess type via generic type argument, please override Spec::getType'
      """
