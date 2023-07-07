Feature: System Default Value

  Scenario: first and second system default value
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

  Rule: recursive system default value

    Background:
      And declaration jFactory =
      """
      new JFactory(new DataRepository() {
        @Override
        public void save(Object object) {
        }
        @Override
        public <T> Collection<T> queryAll(Class<T> type) {
            return new ArrayList<T>();
        }
        @Override
        public void clear() {
        }
      });
      """

    Scenario Outline: byte/Byte/short/Short/boolean/Boolean/LocalTime, for int/Integer/long/Long single test runs too long time so have to be ignored
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When register:
      """
      IntStream.range(0, <sequence>).forEach(i -> {
        jFactory.type(Bean.class).create();
      });
      """
      When build:
      """
      jFactory.type(Bean.class).create();
      """
      Then the result should:
      """
      value: <value>
      """
      Examples:
        | type                | sequence            | value      |
        | byte                | Byte.MAX_VALUE-1    | 127y       |
        | byte                | Byte.MAX_VALUE      | -128       |
        | byte                | Byte.MAX_VALUE+1    | -127y      |
        | byte                | Byte.MAX_VALUE*2    | -1y        |
        | byte                | Byte.MAX_VALUE*2+1  | 0y         |
        | byte                | Byte.MAX_VALUE*2+2  | 1y         |
        | Byte                | Byte.MAX_VALUE-1    | 127y       |
        | Byte                | Byte.MAX_VALUE      | -128       |
        | Byte                | Byte.MAX_VALUE+1    | -127y      |
        | Byte                | Byte.MAX_VALUE*2    | -1y        |
        | Byte                | Byte.MAX_VALUE*2+1  | 0y         |
        | Byte                | Byte.MAX_VALUE*2+2  | 1y         |
        | short               | Short.MAX_VALUE-1   | 32767s     |
        | short               | Short.MAX_VALUE     | -32768     |
        | short               | Short.MAX_VALUE+1   | -32767s    |
        | short               | Short.MAX_VALUE*2   | -1s        |
        | short               | Short.MAX_VALUE*2+1 | 0s         |
        | short               | Short.MAX_VALUE*2+2 | 1s         |
        | Short               | Short.MAX_VALUE-1   | 32767s     |
        | Short               | Short.MAX_VALUE     | -32768     |
        | Short               | Short.MAX_VALUE+1   | -32767s    |
        | Short               | Short.MAX_VALUE*2   | -1s        |
        | Short               | Short.MAX_VALUE*2+1 | 0s         |
        | Short               | Short.MAX_VALUE*2+2 | 1s         |
        | boolean             | 2                   | true       |
        | Boolean             | 2                   | true       |
        | java.time.LocalTime | 24*60*60            | '00:00:01' |

    Scenario: Enum
      Given the following bean class:
      """
      public class Bean {
          public EnumType enumValue;
          public enum EnumType {
            A, B
          }
      }
      """
      When register:
      """
      IntStream.range(0, 2).forEach(i -> {
        jFactory.type(Bean.class).create();
      });
      """
      When build:
      """
      jFactory.type(Bean.class).create();
      """
      Then the result should:
      """
      enumValue: A
      """