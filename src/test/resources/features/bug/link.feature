Feature: link

  Scenario: should ignore parent property link when specify sub property(create sub)
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
          link("order.user", "user");
        }
      }
    """
    And the following spec class:
    """
      public class OneOrder extends Spec<Order> {
      }
    """
    And build:
    """
      jFactory.spec(OneOrder.class).property("user.name", "Tom").property("id", "001").create();
    """
    When build:
    """
      jFactory.spec(OneRecorder.class).property("user.name", "Lucy").property("order.id", "001").create();
    """
    Then the result should:
    """
      user.name: Lucy
    """

#    TODO link / dep
#  TODO link merge objectproducer