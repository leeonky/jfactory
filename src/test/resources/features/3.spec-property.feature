Feature: define spec

  Background:
    Given declaration jFactory =
    """
    new JFactory();
    """

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

    Scenario: specify property by spec class and create sub object with out query during creation
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

    Scenario: spec class with trait method
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
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Trait
        public void hello() {
          property("value").value("hello");
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
      bean.value= hello
      """

    Scenario: 'query before creation' for spec class and trait method
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
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Trait
        public void hello() {
          property("value").value("hello");
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
      When operate:
      """
      jFactory.createAs(ABeanWrapper.class);
      jFactory.createAs(ABeanWrapper.class);
      """
      Then "jFactory.type(Bean.class).queryAll()" should
      """
      ::size= 1
      """

    Scenario: sub object query should be matched when empty trait
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
          property("bean").from(ABean.class).which(spec -> {});
        }
      }
      """
      When operate:
      """
      jFactory.createAs(ABeanWrapper.class);
      jFactory.createAs(ABeanWrapper.class);
      """
      Then "jFactory.type(Bean.class).queryAll()" should
      """
      ::size= 1
      """
