Feature: define spec

  Background:
    Given declaration jFactory =
    """
    new JFactory();
    """

  Rule: value

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
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value").value("world");
          property("bean").value(() -> instance().reference().get());
        }
      }
      """
      When build:
      """
      jFactory.createAs(ABean.class);
      """
      Then the result should:
      """
      bean.value= world
      """

  Rule: default value

    Scenario: support define default value
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
          property("value").defaultValue("hello");
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

    Scenario: define lazy mode default value
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
          property("value").defaultValue(() -> "hello");
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
          property("value").defaultValue(() -> null);
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

    Scenario: property value override default value
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
          property("value").value("override");
          property("value").defaultValue("hello");
        }
      }
      """
      When build:
      """
      jFactory.createAs(ABean.class);
      """
      Then the result should:
      """
      value= override
      """

  Rule: sub spec

    Scenario: create sub object with out query during creation when use `is` spec and trait method
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
      }
      """
      And declaration jFactory =
      """
      new JFactory(new DataRepository() {
        @Override
        public void save(Object object) {
        }
        @Override
        public <T> Collection<T> queryAll(Class<T> type) {
            throw new java.lang.RuntimeException("Failed!");
        }
        @Override
        public void clear() {
        }
      });
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value").value("bean");
        }
      }
      """
      And the following spec class:
      """
      public class ABeanWrapper extends Spec<BeanWrapper> {
        @Override
        public void main() {
          property("bean").is(ABean.class);
        }
      }
      """
      When build:
      """
      jFactory.createAs(ABeanWrapper.class);
      """
      Then the result should:
      """
      bean.value= bean
      """

    Scenario: create sub object with out query during creation when use `is` with spec and trait name
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2;
      }
      """
      Given the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
      }
      """
      And declaration jFactory =
      """
      new JFactory(new DataRepository() {
        @Override
        public void save(Object object) {
        }
        @Override
        public <T> Collection<T> queryAll(Class<T> type) {
            throw new java.lang.RuntimeException("Failed!");
        }
        @Override
        public void clear() {
        }
      });
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value2").value("bean");
        }

        @Trait
        public void hello() {
          property("value1").value("hello");
        }
      }
      """
      And the following spec class:
      """
      public class ABeanWrapper extends Spec<BeanWrapper> {
        @Override
        public void main() {
          property("bean").is("hello", "ABean");
        }
      }
      """
      When build:
      """
      jFactory.createAs(ABeanWrapper.class);
      """
      Then the result should:
      """
      bean= {
        value1= hello
        value2= bean
      }
      """

    Scenario: create sub object with out query during creation when use `from which`
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2;
      }
      """
      Given the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
      }
      """
      And declaration jFactory =
      """
      new JFactory(new DataRepository() {
        @Override
        public void save(Object object) {
        }
        @Override
        public <T> Collection<T> queryAll(Class<T> type) {
            throw new java.lang.RuntimeException("Failed!");
        }
        @Override
        public void clear() {
        }
      });
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value2").value("bean");
        }

        @Trait
        public void hello() {
          property("value1").value("hello");
        }
      }
      """
      And the following spec class:
      """
      public class ABeanWrapper extends Spec<BeanWrapper> {
        @Override
        public void main() {
          property("bean").from(ABean.class).which(ABean::hello);
        }
      }
      """
      When build:
      """
      jFactory.createAs(ABeanWrapper.class);
      """
      Then the result should:
      """
      bean= {
        value1= hello
        value2= bean
      }
      """

    Scenario: should query exist object when use `from and` during creation
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2;
      }
      """
      Given the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
      }
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value2").value("bean");
        }

        @Trait
        public void hello() {
          property("value1").value("hello");
        }
      }
      """
      And the following spec class:
      """
      public class ABeanWrapper extends Spec<BeanWrapper> {
        @Override
        public void main() {
          property("bean").from(ABean.class).and(builder -> builder.property("value1", "query"));
        }
      }
      """
      And build:
      """
      jFactory.type(Bean.class)
        .property("value1", "query")
        .property("value2", "cached")
        .create();
      """
      When build:
      """
      jFactory.createAs(ABeanWrapper.class);
      """
      Then the result should:
      """
      bean= {
        value1= query
        value2= cached
      }
      """

    Scenario: raise error when incomplete method invoke
      Given the following bean class:
      """
      public class Bean {
      }
      """
      Given the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
      }
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
      }
      """
      And the following spec class:
      """
      public class ABeanWrapper extends Spec<BeanWrapper> {
        @Override
        public void main() {
          property("bean").from(ABean.class);
        }
      }
      """
      When build:
      """
      jFactory.createAs(ABeanWrapper.class);
      """
      Then should raise error:
      """
      message: "Invalid property spec:
      \tsrc.test.ABeanWrapper.main(ABeanWrapper.java:9)
      Should finish method chain with `and` or `which`:
      \tproperty().from().which()
      \tproperty().from().and()
      Or use property().is() to create object with only spec directly."
      """
