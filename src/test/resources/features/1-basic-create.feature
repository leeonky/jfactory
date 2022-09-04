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

    Scenario: default value types - support generate default value for these types under the current version of JFactory
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
      When operate:
      """
      jFactory.type(Bean.class).property("str", "hello").create();
      jFactory.type(BeanWrapper.class).property("bean.str", "hello").create();
      """
      And "jFactory.type(Bean.class).queryAll()" should
      """
      ::size= 1
      """

    Scenario: list matching - list matching means some of element matches, not all list elements equal
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
      When operate:
      """
      jFactory.type(BeansWrapper.class)
        .property("beans.beans[0].str", "hello")
        .property("beans.beans[1].str", "world")
        .create();
      jFactory.type(BeansWrapper.class).property("beans.beans[1].str", "world").create();
      """
      Then "jFactory.type(Beans.class).queryAll()" should
      """
      ::size= 1
      """
      When build:
      """
      jFactory.type(BeansWrapper.class).property("beans.beans[1].str", "not match").create();
      """
      Then "jFactory.type(Beans.class).queryAll()" should
      """
      ::size= 2
      """

    Scenario: customized repo - use customized repo in build
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
              return null;
          }
          @Override
          public void clear() {
          }
      });
      """
      And the following bean class:
      """
      public class Bean {
        public String str;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("str", "hello").create();
      """
      Then the list in repo should:
      """
      : [{
        str= hello
        class.simpleName= Bean
      }]
      """

    Scenario: order of repo saving - save object and sub objects in right order
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
      And the following bean class:
      """
      public class Bean {
        public String stringValue;
      }
      """
      And the following bean class:
      """
      public class Beans {
        public Bean bean;
      }
      """
      And the following bean class:
      """
      public class BeansWrapper {
        public Beans beans;
      }
      """
      When build:
      """
      jFactory.type(BeansWrapper.class).property("beans.bean.stringValue", "hello").create();
      """
      Then the list in repo should:
      """
      : [{
        stringValue= hello
        class.simpleName= Bean
      }{
        class.simpleName= Beans
      }{
        class.simpleName= BeansWrapper
      }]
      """

  Rule: spec

    Scenario: in lambda - define spec and naming spec(trait) in lambda
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class)
        .spec("hello", instance -> instance.spec().property("value1").value("hello"))
        .spec(instance -> instance.spec().property("value2").value("world"));
      """
      When build:
      """
      jFactory.type(Bean.class).create();
      """
      Then the result should:
      """
      = {
        value1= /^value1.*/
        value2= "world"
      }
      """
      When build:
      """
      jFactory.type(Bean.class).traits("hello").create();
      """
      Then the result should:
      """
      = {
        value1= hello
        value2= world
      }
      """

  Rule: params

    Scenario: use params - use params in spec
      Given the following bean class:
      """
      public class Bean {
        public String str1, str2;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
        .property("str1").value((Object) instance.param("p1"))
        .property("str2").value((Object) instance.param("p2")));
      """
      When build:
      """
      jFactory.type(Bean.class).arg("p1", "foo").create();
      """
      Then the result should:
      """
      str1= foo
      """
      When build:
      """
      jFactory.type(Bean.class).args(new HashMap<String, String>() {{
        put("p1", "hello");
        put("p2", "world");
      }}).create();
      """
      Then the result should:
      """
      = {
        str1= hello
        str2= world
      }
      """
      When build:
      """
      jFactory.type(Bean.class).args(arg("p1", "hello").arg("p2", "world")).create();
      """
      Then the result should:
      """
      = {
        str1= hello
        str2= world
      }
      """

    Scenario: default value - support default params
      Given the following bean class:
      """
      public class Bean {
        public String str1, str2;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
        .property("str1").value((Object) instance.param("p1", "default1"))
        .property("str2").value((Object) instance.param("p2", "default2")));
      """
      When build:
      """
      jFactory.type(Bean.class).create();
      """
      Then the result should:
      """
      = {
        str1= default1
        str2= default2
      }
      """

    Scenario: nested arg - support nested args
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
      And the following bean class:
      """
      public class BeanWrapperWrapper {
        public BeanWrapper beanWrapper;
      }
      """
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
        .property("str").value((Object) instance.param("p")));
      jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
        .property("bean").byFactory());
      jFactory.factory(BeanWrapperWrapper.class).spec(instance -> instance.spec()
        .property("beanWrapper").byFactory());
      """
      When build:
      """
      jFactory.type(BeanWrapper.class).args("bean", arg("p", "hello")).create();
      """
      Then the result should:
      """
      bean.str= hello
      """
      When build:
      """
      jFactory.type(BeanWrapperWrapper.class).args("beanWrapper.bean", arg("p", "hello")).create();
      """
      Then the result should:
      """
      beanWrapper.bean.str= hello
      """

    Scenario: in spec class - use args in spec class
      Given the following bean class:
      """
      public class Bean {
        public String str;
      }
      """
      And the following spec class:
      """
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("str").value((Object)param("p"));
        }
      }
      """
      When build:
      """
      jFactory.spec(ABean.class).arg("p", "hello").create();
      """
      Then the result should:
      """
      str= hello
      """

    Scenario: fetch arg - fetch arg in spec
      Given the following bean class:
      """
      public class Bean {
        public String str;
        public Bean setStr(String s) {
          this.str = s;
          return this;
        }
      }
      """
      And the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
        public BeanWrapper setBean(Bean b) {
          this.bean = b;
          return this;
        }
      }
      """
      And the following bean class:
      """
      public class BeanWrapperWrapper {
        public BeanWrapper beanWrapper;
      }
      """
      And register:
      """
      jFactory.factory(BeanWrapperWrapper.class).spec(instance -> instance.spec()
        .property("beanWrapper").value(new BeanWrapper().setBean(new Bean()
          .setStr(instance.params("beanWrapper").params("bean").param("p")))));
      """
      When build:
      """
      jFactory.type(BeanWrapperWrapper.class).args("beanWrapper.bean", arg("p", "hello")).create();
      """
      Then the result should:
      """
      beanWrapper.bean.str= hello
      """

    Scenario: pass args - pass args to another builder
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
      And register:
      """
      jFactory.factory(Bean.class).spec(instance -> instance.spec()
        .property("str").value((Object) instance.param("p")));
      """
      And register:
      """
      jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
        .property("bean").byFactory(builder ->builder.args(instance.params())));
      """
      When build:
      """
      jFactory.type(BeanWrapper.class).arg("p", "hello").create();
      """
      Then the result should:
      """
      bean.str= hello
      """

  Rule: create array

    Scenario: create value array
      When build:
      """
      jFactory.type(String[].class).create();
      """
      Then the result should:
      """
      = []
      """
      When build:
      """
      jFactory.type(String[].class).property("[1]", "hello").create();
      """
      Then the result should:
      """
      = [
        '0#2'
        hello
      ]
      """

    Scenario: create bean array
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      When build:
      """
      jFactory.type(Bean[].class).property("[1].value", "hello").create();
      """
      Then the result should:
      """
      = [null {value= hello}]
      """

  Rule: type reference

    Scenario: create single value type with type reference
      When build:
      """
      jFactory.type(new TypeReference<String>(){}).create();
      """
      Then the result should:
      """
      = ''
      """

    Scenario: create class type with type reference
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      When build:
      """
      jFactory.type(new TypeReference<Bean>(){}).create();
      """
      Then the result should:
      """
      value= value#1
      """

    Scenario: support create empty array list from given collection type
      When build:
      """
      jFactory.type(new TypeReference<ArrayList<String>>(){}).create();
      """
      Then the result should:
      """
      = []
      """

    Scenario: support create empty hash set
      When build:
      """
      jFactory.type(new TypeReference<HashSet<String>>(){}).create();
      """
      Then the result should:
      """
      = []
      """

    Scenario: support create empty array
      When build:
      """
      jFactory.type(new TypeReference<String[]>(){}).create();
      """
      Then the result should:
      """
      = []
      """

    Scenario: use input property
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      When build:
      """
      jFactory.type(new TypeReference<List<Bean>>(){})
        .property("[0].value", "hello").create();
      """
      Then the result should:
      """
      = [{value= hello}]
      """
