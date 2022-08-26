Feature: define spec

  Rule: by property value

    Scenario: lazy value - support use lamdba in property value in spec
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
          property("value").value(() -> "hello");
        }
      }
      """
      When build:
      """
      jFactory.createAs(ABean.class);
      """
      Then the result should:
      """
      value= hello
      """

    Scenario: null value - null should be considered as null object value, not null Supplier<Object>
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
          property("value").value(null);
        }
      }
      """
      When build:
      """
      jFactory.createAs(ABean.class);
      """
      Then the result should:
      """
      value= null
      """

    Scenario: self reference - support reference self in property value lambda
      Given the following bean class:
      """
      public class Bean {
        public Bean bean;
        public String value;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
        .property("value").value("hello")
        .property("bean").value(instance.reference()));
      """
      When build:
      """
      jFactory.create(Bean.class);
      """
      Then the result should:
      """
      bean.value= hello
      """
#      And the following spec class:
#      """
#      public class ABean extends Spec<Bean> {
#        @Override
#        public void main() {
#          property("value").value("world");
#          property("bean").value(() -> instance().reference().get());
#        }
#      }
#      """
#      When build:
#      """
#      jFactory.createAs(ABean.class);
#      """
#      Then the result should:
#      """
#      bean.value= world
#      """
