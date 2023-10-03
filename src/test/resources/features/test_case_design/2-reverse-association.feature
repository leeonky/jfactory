Feature: Reverse Association

  Rule: single object

    Background:
      Given the following bean class:
      """
      public class Person {
        public Passport passport;
      }
      """
      And the following bean class:
      """
      public class Passport {
        public Person person;
        public String number;
      }
      """

    Scenario: no query before create
      Given register:
      """
        jFactory.factory(Person.class).spec(instance -> instance.spec()
          .property("passport").reverseAssociation("person"));
      """
      When build:
      """
        jFactory.type(Person.class).property("passport.number", "001").create();
      """
      Then the result should:
      """
      : {
        passport.number= '001'
        {}= (.passport.person)
      }
      """

    Scenario: should save parent bean first
      Given declaration list =
      """
        new ArrayList<>();
      """
      And declaration jFactory =
      """
      new JFactory(new DataRepository() {
        @Override
        public void save(Object object) {
          list.add(object);
        }
        @Override
        public <T> Collection<T> queryAll(Class<T> type) {
          return Collections.emptyList();
        }
        @Override
        public void clear() {
        }
      });
      """
      And register:
      """
        jFactory.factory(Person.class).spec(instance -> instance.spec()
          .property("passport").reverseAssociation("person"));
      """
      When build:
      """
        jFactory.type(Person.class).property("passport.number", "001").create();
      """
      Then the list in repo should:
      """
      class[].simpleName= [
        Person
        Passport
      ]
      """

    Scenario: query before create should not happen
      Given register:
      """
      jFactory.factory(Person.class).spec(instance -> instance.spec()
        .property("passport").reverseAssociation("person"));
      """
      When operate:
      """
      jFactory.type(Person.class).property("passport.number", "001").create();
      jFactory.type(Person.class).property("passport.number", "001").create();
      """
      Then "jFactory.type(Passport.class).queryAll()" should
      """
      ::size= 2
      """

  Rule: object collection

    Background:
      And the following bean class:
      """
      public class OrderLine {
          public Order order;
          public String info;
      }
      """

    Scenario Outline: no query before create
      Given the following bean class:
      """
      public class Order {
          public <listType> orderLines;
      }
      """
      And register:
      """
      jFactory.factory(Order.class).spec(instance -> instance.spec()
        .property("orderLines").reverseAssociation("order"));
      """
      When build:
      """
      jFactory.type(Order.class).property("orderLines[0].info", "line info").create();
      """
      Then the result should:
      """
      : {
        orderLines.info[]: ['line info']
        {}= (.orderLines[0].order)
      }
      """
      Examples:
        | listType        |
        | OrderLine[]     |
        | List<OrderLine> |
        | Set<OrderLine>  |

    Scenario Outline: should save parent bean first
      Given the following bean class:
      """
      public class Order {
          public <listType> orderLines;
      }
      """
      And declaration list =
      """
        new ArrayList<>();
      """
      And declaration jFactory =
      """
      new JFactory(new DataRepository() {
        @Override
        public void save(Object object) {
          list.add(object);
        }
        @Override
        public <T> Collection<T> queryAll(Class<T> type) {
          return Collections.emptyList();
        }
        @Override
        public void clear() {
        }
      });
      """
      And register:
      """
      jFactory.factory(Order.class).spec(instance -> instance.spec()
        .property("orderLines").reverseAssociation("order"));
      """
      When build:
      """
      jFactory.type(Order.class).property("orderLines[0].info", "line info").create();
      """
      Then the list in repo should:
      """
      class[].simpleName= [
          Order
          OrderLine
      ]
      """
      Examples:
        | listType        |
        | OrderLine[]     |
        | List<OrderLine> |
        | Set<OrderLine>  |

    Scenario Outline: query before create should not happen
      Given the following bean class:
      """
      public class Order {
          public <listType> orderLines;
      }
      """
      Given register:
      """
      jFactory.factory(Order.class).spec(instance -> instance.spec()
        .property("orderLines").reverseAssociation("order"));
      """
      When operate:
      """
      jFactory.type(Order.class).property("orderLines[0].info", "line info").create();
      jFactory.type(Order.class).property("orderLines[0].info", "line info").create();
      """
      Then "jFactory.type(OrderLine.class).queryAll()" should
      """
      ::size= 2
      """
      Examples:
        | listType        |
        | OrderLine[]     |
        | List<OrderLine> |
        | Set<OrderLine>  |
