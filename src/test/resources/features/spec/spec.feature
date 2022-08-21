Feature: use spec

  Scenario: define spec in lambda
    Given the following bean class:
    """
      public class Bean {
        public String value1, value2;
      }
    """
    And register:
    """
      factory(Bean.class)
      .spec("hello", instance -> instance.spec().property("value1").value("hello"))
      .spec(instance -> instance.spec().property("value2").value("world"))
    """
    When create:
    """
      type(Bean.class)
    """
    Then the result should:
    """
    = {
      value1= /^value1.*/
      value2= "world"
    }
    """
    When create:
    """
      type(Bean.class).traits("hello")
    """
    Then the result should:
    """
    = {
      value1= hello
      value2= world
    }
    """

  Scenario: raise error when trait not exist
    Given the following bean class:
    """
      public class Bean {
        public String value;
      }
    """
    When create:
    """
      type(Bean.class).traits("not-exist")
    """
    Then should raise error:
    """
    message= 'Trait `not-exist` not exist'
    """

  Scenario: define spec in class
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
    When create:
    """
      spec(ABean.class).traits("hello")
    """
    Then the result should:
    """
    = {
      value1= hello
      value2= world
    }
    """
    When create as:
    """
      createAs(ABean.class, spec -> spec.hello())
    """
    Then the result should:
    """
    = {
      value1= hello
      value2= world
    }
    """
    When create as:
    """
      createAs("hello", "ABean")
    """
    Then the result should:
    """
    = {
      value1= hello
      value2= world
    }
    """

  Scenario: raise error when trait not exist in spec class
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
    When create as:
    """
      createAs("NotExist")
    """
    Then should raise error:
    """
    message= 'Spec `NotExist` not exist'
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
    When create:
    """
      spec(ABean.class)
    """
    Then the result should:
    """
      value: 0
    """
