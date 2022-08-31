Feature: use spec

  Background:
    Given declaration jFactory =
    """
    new JFactory();
    """

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

  Rule: global spec class

    Scenario: define global spec class as base spec and origin factory as base
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And the following spec class:
      """
      @Global
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value").value("hello");
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).create();
      """
      Then the result should:
      """
      value= hello
      """

    Scenario: support remove global spec class
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2;
      }
      """
      And the following spec class:
      """
      @Global
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value1").value("hello");
        }
      }
      """
      And register:
      """
      jFactory.register(ABean.class);
      jFactory.removeGlobalSpec(Bean.class);
      """
      When build:
      """
      jFactory.type(Bean.class).create();
      """
      Then the result should:
      """
      = {
        value1= 'value1#1'
        value2= 'value2#1'
      }
      """

  Rule: spec inherit

    Scenario: spec class should call base lambda spec
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
          property("value2").value("spec class");
        }
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
        .property("value1").value("lambda spec"));
      """
      When build:
      """
      jFactory.spec(ABean.class).create();
      """
      Then the result should:
      """
      = {
        value1= 'lambda spec'
        value2= 'spec class'
      }
      """

    Scenario: spec class should call global spec class
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2;
      }
      """
      And the following spec class:
      """
      @Global
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value2").value("global spec class");
        }
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
        .property("value1").value("lambda spec"));
      """
      When build:
      """
      jFactory.type(Bean.class).create();
      """
      Then the result should:
      """
      = {
        value1= 'lambda spec'
        value2= 'global spec class'
      }
      """

    Scenario: spec class should call global spec class and lambda spec
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2, value3;
      }
      """
      And the following spec class:
      """
      @Global
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value2").value("global spec class");
        }
      }
      """
      And the following spec class:
      """
      public class AnotherBean extends Spec<Bean> {
        @Override
        public void main() {
          property("value3").value("spec class");
        }
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
        .property("value1").value("lambda spec"));
      """
      When build:
      """
      jFactory.spec(AnotherBean.class).create();
      """
      Then the result should:
      """
      = {
        value1= 'lambda spec'
        value2= 'global spec class'
        value3= 'spec class'
      }
      """

    Scenario: should use base spec in runtime
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean>{
      }
      """
      And the following spec class:
      """
      @Global
      public class ABeanGlobal extends Spec<Bean>{
        @Override
        public void main() {
          property("value").value("global base");
        }
      }
      """
      When build:
      """
      jFactory.createAs(ABean.class);
      """
      Then the result should:
      """
      value= 'global base'
      """

  Rule: spec override

    Scenario: spec class override base lambda spec
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value").value("class spec");
        }
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
        .property("value").value("lambda spec"));
      """
      When build:
      """
      jFactory.spec(ABean.class).create();
      """
      Then the result should:
      """
      value= 'class spec'
      """

    Scenario: global spec class override base lambda spec
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And the following spec class:
      """
      @Global
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value").value("class spec");
        }
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
        .property("value").value("lambda spec"));
      """
      When build:
      """
      jFactory.type(Bean.class).create();
      """
      Then the result should:
      """
      value= 'class spec'
      """

    Scenario: spec class override base global spec class
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And the following spec class:
      """
      @Global
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value").value("global class spec");
        }
      }
      """
      And the following spec class:
      """
      public class AnotherBean extends Spec<Bean> {
        @Override
        public void main() {
          property("value").value("spec class");
        }
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
        .property("value").value("lambda spec"));
      """
      When build:
      """
      jFactory.spec(AnotherBean.class).create();
      """
      Then the result should:
      """
      value= 'spec class'
      """

  Rule: trait override

    Scenario: naming spec(trait) override spec in type
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given register:
      """
      jFactory.factory(Bean.class)
        .spec(instance-> instance.spec().property("value").value("type spec"))
        .spec("hello", instance-> instance.spec().property("value").value("hello"));
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
      jFactory.factory(Bean.class)
        .spec(instance-> instance.spec().property("value").value("type spec"));
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

    Scenario: do not allow duplicated global spec class, so also do not allow a global spec class as base of another global spec class
      Given the following spec class:
      """
      @Global
      public class Spec1 extends Spec<String> {
      }
      """
      And the following spec class:
      """
      @Global
      public class Spec2 extends Spec<String> {
      }
      """
      When build:
      """
      jFactory.createAs(Spec2.class);
      """
      Then should raise error:
      """
      message= 'More than one @Global Spec class `src.test.Spec1` and `src.test.Spec2`'
      """

