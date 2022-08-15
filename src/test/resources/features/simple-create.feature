Feature: basic

  Scenario: create bean with default value
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
    When create type "Bean"
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
      offsetDateTime: '1996-01-23T00:00:01Z'
      zonedDateTime: '1996-01-23T00:00:01Z[Etc/UTC]'
      enumValue: A
    }
    """
    When create type "Bean"
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
      offsetDateTime: '1996-01-23T00:00:02Z'
      zonedDateTime: '1996-01-23T00:00:02Z[Etc/UTC]'
      enumValue: B
    }
    """

  Scenario: create bean with input property
    Given the following bean class:
    """
    public class Bean {
      public String stringValue;
      public int intValue;
    }
    """
    When create type "Bean" with property:
      | stringValue |
      | input-value |
    Then the result should:
    """
    stringValue= input-value
    """
    When create type "Bean" with property:
      | stringValue | intValue |
      | input-value | 100      |
    Then the result should:
    """
    = {
      stringValue= input-value
      intValue= 100
    }
    """

  Scenario: create bean use customer constructor
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
    And register factory:
    """
    jfactory.factory(Bean.class).constructor(arg -> new Bean(100));
    """
    When create type "Bean"
    Then the result should:
    """
    i= 100
    """

