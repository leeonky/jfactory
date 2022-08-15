Feature: dependency

  Scenario: should ignore parent property dependency when specify sub property(create sub)
    Given the following bean class:
    """
    public class Order {
      public String id;
      public User user;
    }
    """
    And the following bean class:
    """
    public class Recorder {
      public User user;
      public Order order;
    }
    """
    And the following bean class:
    """
    public class User {
      public String name;
    }
    """
    And the following spec class:
    """
    public class OneRecorder extends Spec<Recorder> {
      @Override
      public void main() {
        property("user").dependsOn("order.user", o->o);
      }
    }
    """
    And the following spec class:
    """
    public class OneOrder extends Spec<Order> {
    }
    """
    And create "OneOrder" with property:
      | user.name | id  |
      | Tom       | 001 |
    When create "OneRecorder" with property:
      | user.name | order.id |
      | Lucy      | 001      |
    Then the result should:
    """
    user.name: Lucy
    """
