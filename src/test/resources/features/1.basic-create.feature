Feature: basic use

  Background:
    Given declaration jFactory =
    """
    new JFactory();
    """

  Rule: create bean

    Scenario: simple create - create bean with input property
      Given the following bean class:
      """
      public class Bean {
        public String stringValue;
        public int intValue;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("stringValue", "input-value").create();
      """
      Then the result should:
      """
      stringValue= input-value
      """
      When build:
      """
      jFactory.type(Bean.class).properties(new HashMap<String, Object>() {{
        put("stringValue", "input-value");
        put("intValue", 100);
      }}).create();
      """
      Then the result should:
      """
      = {
        stringValue= input-value
        intValue= 100
      }
      """

    Scenario: customize constructor - create bean use customer constructor
      Given the following bean class:
      """
      public class Bean {
        private int i;
        public Bean(int i) {
          this.i = i;
        }

        public int getI() {
          return i;
        }
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).constructor(arg -> new Bean(100));
      """
      When build:
      """
      jFactory.type(Bean.class).create();
      """
      Then the result should:
      """
      i= 100
      """

  Rule: default value

    Scenario: supported types - all supported build-in default value types
      Given the following bean class:
      """
      public class Bean {
        public String stringValue;
        public int intValue;
        public Integer boxedIntValue;
        public short shortValue;
        public Short boxedShortValue;
        public byte byteValue;
        public Byte boxedbyteValue;
        public long longValue;
        public Long boxedLongvalue;
        public float floatValue;
        public Float boxedFloatValue;
        public double doubleValue;
        public Double boxedDoubleValue;
        public boolean boolValue;
        public Boolean boxedBoolValue;
        public BigInteger bigInt;
        public BigDecimal bigDec;
        public UUID uuid;
        public Date date;
        public java.time.Instant instant;
        public java.time.LocalDate localDate;
        public java.time.LocalTime localTime;
        public java.time.LocalDateTime localDateTime;
        public java.time.OffsetDateTime offsetDateTime;
        public java.time.ZonedDateTime zonedDateTime;
        public EnumType enumValue;

        public enum EnumType {
          A, B
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).create();
      """
      Then the result should:
      """
      = {
        stringValue= stringValue#1
        intValue= 1
        boxedIntValue= 1
        shortValue= 1s
        boxedShortValue= 1s
        byteValue= 1y
        boxedbyteValue= 1y
        longValue= 1L
        boxedLongvalue= 1L
        floatValue= 1.0f
        boxedFloatValue= 1.0f
        doubleValue= 1.0d
        boxedDoubleValue= 1.0d
        boolValue= true
        boxedBoolValue= true
        bigInt= 1bi
        bigDec= 1bd
        uuid: '00000000-0000-0000-0000-000000000001'
        date.toInstant: '1996-01-24T00:00:00Z'
        instant: '1996-01-23T00:00:01Z'
        localDate: '1996-01-24'
        localTime: '00:00:01'
        localDateTime: '1996-01-23T00:00:01'
        offsetDateTime.toInstant: '1996-01-23T00:00:01Z'
        zonedDateTime.toInstant: '1996-01-23T00:00:01Z'
        enumValue: A
      }
      """
      When build:
      """
      jFactory.type(Bean.class).create();
      """
      Then the result should:
      """
      = {
        stringValue= stringValue#2
        intValue= 2
        boxedIntValue= 2
        shortValue= 2s
        boxedShortValue= 2s
        byteValue= 2y
        boxedbyteValue= 2y
        longValue= 2L
        boxedLongvalue= 2L
        floatValue= 2.0f
        boxedFloatValue= 2.0f
        doubleValue= 2.0d
        boxedDoubleValue= 2.0d
        boolValue= false
        boxedBoolValue= false
        bigInt= 2bi
        bigDec= 2bd
        uuid: '00000000-0000-0000-0000-000000000002'
        date.toInstant: '1996-01-25T00:00:00Z'
        instant: '1996-01-23T00:00:02Z'
        localDate: '1996-01-25'
        localTime: '00:00:02'
        localDateTime: '1996-01-23T00:00:02'
        offsetDateTime.toInstant: '1996-01-23T00:00:02Z'
        zonedDateTime.toInstant: '1996-01-23T00:00:02Z'
        enumValue: B
      }
      """

    Scenario: customized - define default value strategy by type
      Given the following bean class:
      """
      public class Bean {
        public String str;
      }
      """
      When register:
      """
      jFactory.registerDefaultValueFactory(String.class, new DefaultValueFactory<String>() {
        @Override
          public <T> String create(BeanClass<T> beanType, SubInstance<T> instance) {
            return "hello";
          }
        });
      """
      And build:
      """
      jFactory.type(Bean.class).create();
      """
      Then the result should:
      """
      str= hello
      """

    Scenario: skip - support skip default value generation
      Given the following bean class:
      """
      public class Bean {
        public String str;
      }
      """
      When register:
      """
      jFactory.ignoreDefaultValue(propertyWriter -> "str".equals(propertyWriter.getName()));
      """
      And build:
      """
      jFactory.type(Bean.class).create();
      """
      Then the result should:
      """
      str= null
      """

  Rule: data repo

    Scenario: save/query/query all - save bean after create bean
      Given the following bean class:
      """
      public class Bean {
        public String str;
      }
      """
      And build:
      """
      jFactory.type(Bean.class).property("str", "hello").create();
      """
      When build:
      """
      jFactory.type(Bean.class).property("str", "hello").query();
      """
      Then the result should:
      """
      str= hello
      """
      When build:
      """
      jFactory.type(Bean.class).property("str", "hello").queryAll();
      """
      Then the result should:
      """
      str[]= [hello]
      """

    Scenario: query empty - query nothing when property miss
      Given the following bean class:
      """
      public class Bean {
        public String str;
      }
      """
      And build:
      """
      jFactory.type(Bean.class).property("str", "hello").create();
      """
      When build:
      """
      jFactory.type(Bean.class).property("str", "not match").query();
      """
      Then the result should:
      """
      = null
      """
      When build:
      """
      jFactory.type(Bean.class).property("str", "not match").queryAll();
      """
      Then the result should:
      """
      = []
      """

    Scenario: clear repo - query nothing when repo is cleared before query
      Given the following bean class:
      """
      public class Bean {
        public String str;
      }
      """
      And build:
      """
      jFactory.type(Bean.class).property("str", "hello").create();
      """
      And operate:
      """
      jFactory.getDataRepository().clear();
      """
      When build:
      """
      jFactory.type(Bean.class).property("str", "hello").query();
      """
      Then the result should:
      """
      = null
      """

    Scenario: property chain - save and query with property chain
      Given the following bean class:
      """
      public class Bean {
        public String str;
      }
      """
      And the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
      }
      """
      And build:
      """
      jFactory.type(BeanWrapper.class).property("bean.str", "hello").create();
      """
      When build:
      """
      jFactory.type(BeanWrapper.class).property("bean.str", "hello").query();
      """
      Then the result should:
      """
      bean.str= hello
      """
      When build:
      """
      jFactory.type(Bean.class).property("str", "hello").query();
      """
      Then the result should:
      """
      str= hello
      """
      When build:
      """
      jFactory.type(BeanWrapper.class).property("bean.str", "not hello").query();
      """
      Then the result should:
      """
      = null
      """

    Scenario: data relation - query and use saved been as sub object
      Given the following bean class:
      """
      public class Bean {
        public String str;
      }
      """
      And the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
      }
      """
      And create:
      """
      type(Bean.class).property("str", "hello")
      """
      When create:
      """
      type(BeanWrapper.class).property("bean.str", "hello")
      """
      Then query all:
      """
      type(Bean.class)
      """
      And the result should:
      """
      ::size= 1
      """

    Scenario: list matching - matches means some of element matches, not all list elements equal
      Given the following bean class:
      """
      public class Bean {
        public String str;
      }
      """
      And the following bean class:
      """
      public class Beans {
        public Bean[] beans;
      }
      """
      And the following bean class:
      """
      public class BeansWrapper {
        public Beans beans;
      }
      """
      And build:
      """
      jFactory.type(BeansWrapper.class)
        .property("beans.beans[0].str", "hello")
        .property("beans.beans[1].str", "world")
        .create();
      """
      And build:
      """
      jFactory.type(BeansWrapper.class).property("beans.beans[1].str", "world").create();
      """
      When build:
      """
      jFactory.type(Beans.class).queryAll();
      """
      Then the result should:
      """
      ::size= 1
      """
      When build:
      """
      jFactory.type(BeansWrapper.class).property("beans.beans[1].str", "not match").create();
      """
      And query all:
      """
      type(Beans.class)
      """
      Then the result should:
      """
      ::size= 2
      """

