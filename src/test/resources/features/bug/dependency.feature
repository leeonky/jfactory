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
    And create:
    """
      spec(OneOrder.class).property("user.name", "Tom").property("id", "001")
    """
    When create:
    """
      spec(OneRecorder.class).property("user.name", "Lucy").property("order.id", "001")
    """
    Then the result should:
    """
      user.name: Lucy
    """
