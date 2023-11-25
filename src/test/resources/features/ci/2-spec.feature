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
      public class BeanSpec extends Spec<Bean> {

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
      jFactory.spec(BeanSpec.class).traits("hello").create();
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
      jFactory.createAs(BeanSpec.class, spec -> spec.hello());
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
      jFactory.createAs("hello", "BeanSpec");
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
      public class BeanSpec extends Spec<Bean> {
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
      public class BeanSpec extends Spec<Bean> {
        @Override
        public void main() {
          property("value1").value("hello");
        }
      }
      """
      And register:
      """
      jFactory.register(BeanSpec.class);
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
      public class BeanSpec extends Spec<Bean> {
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
      jFactory.spec(BeanSpec.class).create();
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
      public class BeanSpec extends Spec<Bean> {
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
      public class BeanSpec extends Spec<Bean> {
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
      public class BeanSpec extends Spec<Bean>{
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
      jFactory.createAs(BeanSpec.class);
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
      public class BeanSpec extends Spec<Bean> {
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
      jFactory.spec(BeanSpec.class).create();
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
      public class BeanSpec extends Spec<Bean> {
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
      public class BeanSpec extends Spec<Bean> {
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
      public class BeanSpec extends Spec<Bean> {
        @Trait
        public void hello() {
          property("value").value("hello");
        }
      }
      """
      When build:
      """
      jFactory.spec("hello", "BeanSpec").create();
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
      public class BeanSpec extends Spec<Bean> {
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
      jFactory.spec("hello", "BeanSpec").create();
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
      public class BeanSpec extends Spec<Bean> {
        @Override
        public void main() {
          property("value").value("type spec");
        }

        @Trait
        public BeanSpec hello() {
          property("value").value("hello");
          return this;
        }
      }
      """
      When build:
      """
      jFactory.createAs(BeanSpec.class, spec -> spec.hello());
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
      public class BeanSpec extends Spec<Bean> {
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
      public class BeanSpec extends Spec<Bean> {
        private static int i = 0;

        @Override
        public void main() {
          property("value").value(i++);
        }
      }
      """
      When build:
      """
      jFactory.spec(BeanSpec.class).create();
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
      public class BeanSpec extends Spec<Bean> {
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
      message= 'More than one @Global Spec class `#package#Spec1` and `#package#Spec2`'
      """

  Rule: args in trait

    Scenario: support use parameter in Trait lambda
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec("input-(.+)", ins -> {
          ins.spec().property("value").value(ins.traitParam(0));
      });
      """
      When build:
      """
      jFactory.type(Bean.class).traits("input-hello").create();
      """
      Then the result should:
      """
      value= hello
      """

    Scenario: support multi args in trait lambda
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec("input-(.+)-(.+)", ins -> {
          ins.spec().property("value").value(ins.traitParam(0)+ "_" +  ins.traitParam(1));
      });
      """
      When build:
      """
      jFactory.type(Bean.class).traits("input-hello-world").create();
      """
      Then the result should:
      """
      value= hello_world
      """

    Scenario: support multi args and multi traits in trait lambda
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2, value3;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec("input1-(.+)-(.+)", ins -> {
          ins.spec().property("value1").value(ins.traitParam(0)+ "_1_" +  ins.traitParam(1));
      });
      jFactory.factory(Bean.class).spec("input3-(.+)-(.+)", ins -> {
          ins.spec().property("value3").value(ins.traitParam(0)+ "_3_" +  ins.traitParam(1));
      });
      jFactory.factory(Bean.class).spec("input-value2", ins -> {
          ins.spec().property("value2").value("v2");
      });
      """
      When build:
      """
      jFactory.type(Bean.class).traits("input1-hello-world", "input-value2", "input3-goodbye-world").create();
      """
      Then the result should:
      """
      value{}: {
        '1'= hello_1_world
        '2'= v2
        '3'= goodbye_3_world
      }
      """

    Scenario: support args in trait method
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given the following spec class:
      """
      public class BeanSpec extends Spec<Bean> {

        @Trait("input-(.+)-(.+)")
        public void input(int i, int j) {
          property("value").value(i+j);
        }
      }
      """
      When build:
      """
      jFactory.spec(BeanSpec.class).traits("input-1-2").create();
      """
      Then the result should:
      """
      value= '3'
      """

    Scenario: ignore trait args when count different
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given the following spec class:
      """
      public class BeanSpec extends Spec<Bean> {
        @Trait("input-(.+)-(.+)")
        public void input(int i) {
          property("value").value(i);
        }
      }
      """
      When build:
      """
      jFactory.spec(BeanSpec.class).traits("input-1-2").create();
      """
      Then the result should:
      """
      value= '1'
      """

    Scenario: should use trait with full name matched first
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec("input-(.+)", ins -> {
        throw new RuntimeException("failed");
      }).spec("input-hello", ins -> {
          ins.spec().property("value").value("hello");
      });
      """
      When build:
      """
      jFactory.type(Bean.class).traits("input-hello").create();
      """
      Then the result should:
      """
      value= hello
      """

    Scenario: raise error when more than one pattern matched
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec("input-(.+)", ins -> {
        throw new RuntimeException("failed");
      }).spec("input-(.*)", ins -> {
        throw new RuntimeException("failed");
      });
      """
      When build:
      """
      jFactory.type(Bean.class).traits("input-hello").create();
      """
      Then should raise error:
      """
      message= ```
               Ambiguous trait pattern: input-hello, candidates are:
                 input-(.+)
                 input-(.*)
               ```
      """

