Feature: spec

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
    When create "ABean"
    Then the result should:
    """
    value: 0
    """
