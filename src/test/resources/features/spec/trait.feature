Feature: trait

  Background:
    Given the following bean class:
    """
    public class Bean {
      public String value;
    }
    """

  Scenario: naming spec(trait) override spec in type
    Given register factory:
    """
    jfactory.factory(Bean.class)
      .spec(instance-> instance.spec().property("value").value("type spec"))
      .spec("hello", instance-> instance.spec().property("value").value("hello"));
    """
    When create type "Bean" with traits "hello"
    Then the result should:
    """
    value: hello
    """

  Scenario: trait in spec class override spec in type
    Given register factory:
    """
    jfactory.factory(Bean.class)
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
    When create "hello ABean"
    Then the result should:
    """
    value: hello
    """

  Scenario: trait in spec class override spec in spec class
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
    When create "hello ABean"
    Then the result should:
    """
    value: hello
    """

  Scenario: trait in spec class override spec in spec instance
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
    When create from spec "ABean" with:
    """
    spec.hello()
    """
    Then the result should:
    """
    value: hello
    """

  Scenario: trait in spec class override spec in global spec class
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
    When create type "Bean" with traits "hello"
    Then the result should:
    """
    value: hello
    """
