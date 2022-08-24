Feature: use spec

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
