Feature: trait

  Scenario: override spec in type
    Given the following bean class:
    """
    public class Bean {
      public String value;
    }
    """
    And spec of type "Bean"
    """
      instance.spec().property("value").value("type spec");
    """
    And trait "hello" of type "Bean"
    """
      instance.spec().property("value").value("hello");
    """
    When create type "Bean" with traits "hello"
    Then the result should:
    """
    value: hello
    """
