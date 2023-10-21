Feature: Basic Create

  Background:
    Given declaration jFactory =
    """
    new JFactory();
    """
    Given the following bean class:
      """
      public class CustomObject {
        public String anyProperty;
      }
      """
    Given the following bean class:
      """
      public class NumberBase extends Number {
        public double doubleValue() {
            return 123.07d;
        }
        public float floatValue() {
            return 123.07f;
        }
        public int intValue() {
            return 43;
        }
        public long longValue() {
            return 43l;
        }
      }
      """

  Rule: Create bean with property("name", "value") and properties(Map<String, Object>)

    Scenario Outline: Create bean with property("name", "value") - All base type = except special cases
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", <input>).create();
      """
      Then the result should:
      """
      value= <expected>
      """
      When build:
      """
      jFactory.type(Bean.class).properties(new HashMap(){{
        put("value", <input>);
        }}).create();
      """
      Then the result should:
      """
      value= <expected>
      """
      Examples:
        | type       | input                    | expected    |
        | String     | "input-value"            | input-value |
        | int        | 42                       | 42          |
        | int        | "42"                     | 42          |
        | Integer    | new Integer(42)          | 42          |
        | Integer    | "42"                     | 42          |
        | short      | Short.parseShort("42")   | 42s         |
        | short      | "42"                     | 42s         |
        | Short      | Short.valueOf("42")      | 42s         |
        | Short      | "42"                     | 42s         |
        | byte       | Byte.parseByte("42")     | 42y         |
        | byte       | "42"                     | 42y         |
        | Byte       | Byte.valueOf("42")       | 42y         |
        | Byte       | "42"                     | 42y         |
        | long       | 42l                      | 42l         |
        | long       | "42"                     | 42l         |
        | Long       | new Long(42l)            | 42l         |
        | Long       | "42"                     | 42l         |
        | float      | 123.06f                  | 123.06f     |
        | float      | "123.06"                 | 123.06f     |
        | Float      | new Float(123.06f)       | 123.06f     |
        | Float      | "123.06"                 | 123.06f     |
        | double     | 123.06d                  | 123.06d     |
        | double     | "123.06"                 | 123.06d     |
        | Double     | new Double(123.06d)      | 123.06d     |
        | Double     | "123.06"                 | 123.06d     |
        | boolean    | true                     | true        |
        | boolean    | "true"                   | true        |
        | boolean    | new Boolean(false)       | false       |
        | Boolean    | true                     | true        |
        | Boolean    | "true"                   | true        |
        | BigInteger | BigInteger.valueOf(42)   | 42bi        |
        | BigInteger | "42"                     | 42bi        |
        | BigDecimal | new BigDecimal("123.06") | 123.06bd    |
        | BigDecimal | "123.06"                 | 123.06bd    |

    Scenario Outline: Create bean with property("name", "value") - All base type : except special cases
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", <input>).create();
      """
      Then the result should:
      """
      value: <expected>
      """
      When build:
      """
      jFactory.type(Bean.class).properties(new HashMap(){{
        put("value", <input>);
        }}).create();
      """
      Then the result should:
      """
      value: <expected>
      """
      Examples:
        | type                     | input                                                                            | expected                               |
        | UUID                     | "123e4567-e89b-12d3-a456-426655440000"                                           | '123e4567-e89b-12d3-a456-426655440000' |
        | UUID                     | UUID.fromString("123e4567-e89b-12d3-a456-426655440000")                          | '123e4567-e89b-12d3-a456-426655440000' |
        | java.time.Instant        | java.time.Instant.EPOCH                                                          | '1970-01-01T00:00:00Z'                 |
        | java.time.Instant        | "2009-07-24T02:03:04Z"                                                           | '2009-07-24T02:03:04Z'                 |
        | java.time.LocalDate      | java.time.LocalDate.of(1978, 5, 3)                                               | '1978-05-03'                           |
        | java.time.LocalDate      | "1978-05-03"                                                                     | '1978-05-03'                           |
        | java.time.LocalTime      | java.time.LocalTime.of(12, 30, 45)                                               | '12:30:45'                             |
        | java.time.LocalTime      | "12:30:45"                                                                       | '12:30:45'                             |
        | java.time.LocalDateTime  | java.time.LocalDateTime.of(1978, 5, 3, 12, 30, 45)                               | '1978-05-03T12:30:45'                  |
        | java.time.LocalDateTime  | "1978-05-03T12:30:45"                                                            | '1978-05-03T12:30:45'                  |
        | java.time.OffsetDateTime | java.time.OffsetDateTime.of(1978, 5, 3, 12, 30, 45, 0, java.time.ZoneOffset.UTC) | '1978-05-03T12:30:45Z'                 |
        | java.time.OffsetDateTime | "1978-05-03T12:30:45Z"                                                           | '1978-05-03T12:30:45Z'                 |
        | java.time.ZonedDateTime  | java.time.ZonedDateTime.of(1978, 5, 3, 12, 30, 45, 0, java.time.ZoneOffset.UTC)  | '1978-05-03T12:30:45Z'                 |
        | java.time.ZonedDateTime  | "1978-05-03T12:30:45Z"                                                           | '1978-05-03T12:30:45Z'                 |
        | java.time.YearMonth      | "1978-05"                                                                        | '1978-05'                              |

    Scenario Outline: Create bean with property("name", "value") - custom type
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", <input>).create();
      """
      Then the result should:
      """
      value: <expected>
      """
      When build:
      """
      jFactory.type(Bean.class).properties(new HashMap(){{
        put("value", <input>);
        }}).create();
      """
      Then the result should:
      """
      value: <expected>
      """
      Examples:
        | type                | input                           | expected              |
        | java.time.YearMonth | "1978-05"                       | '1978-05'             |
        | java.time.YearMonth | java.time.YearMonth.of(1978, 5) | '1978-05'             |
        | CustomObject        | new CustomObject()              | { anyProperty= null } |

    Scenario Outline: Create bean with property("name", "value") - Date
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", <input>).create();
      """
      Then the result should:
      """
      value.toInstant: <expected>
      """
      When build:
      """
      jFactory.type(Bean.class).properties(new HashMap(){{
        put("value", <input>);
        }}).create();
      """
      Then the result should:
      """
      value.toInstant: <expected>
      """
      Examples:
        | type | input        | expected               |
        | Date | new Date(0)  | '1970-01-01T00:00:00Z' |
        | Date | "1978-05-03" | '1978-05-03T00:00:00Z' |

    Scenario: Create bean with property("name", "value") - Object->String
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given the following bean class:
      """
      public class ToBeConverted {
        public String toString() {
          return "converted-value";
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", new ToBeConverted()).create();
      """
      Then the result should:
      """
      value= converted-value
      """
      When build:
      """
      jFactory.type(Bean.class).properties(new HashMap(){{
        put("value", new ToBeConverted());
        }}).create();
      """
      Then the result should:
      """
      value= converted-value
      """

    Scenario Outline: create bean with property("name", "value") - Number->byte/Byte/short/Short/int/Integer/long/Long/float/Float/double/Double/BigInteger/BigDecimal
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", <input>).create();
      """
      Then the result should:
      """
      value= <expected>
      """
      When build:
      """
      jFactory.type(Bean.class).properties(new HashMap(){{
        put("value", <input>);
        }}).create();
      """
      Then the result should:
      """
      value= <expected>
      """
      Examples:
        | type       | input                    | expected |
        | byte       | Byte.valueOf("42")       | 42y      |
        | byte       | Short.valueOf("42")      | 42y      |
        | byte       | Integer.valueOf(42)      | 42y      |
        | byte       | Long.valueOf(42l)        | 42y      |
        | byte       | Float.valueOf(42f)       | 42y      |
        | byte       | Double.valueOf(42d)      | 42y      |
        | byte       | BigInteger.valueOf(42)   | 42y      |
        | byte       | BigDecimal.valueOf(42)   | 42y      |
        | Byte       | Byte.valueOf("42")       | 42y      |
        | Byte       | Short.valueOf("42")      | 42y      |
        | Byte       | Integer.valueOf(42)      | 42y      |
        | Byte       | Long.valueOf(42l)        | 42y      |
        | Byte       | Float.valueOf(42f)       | 42y      |
        | Byte       | Double.valueOf(42d)      | 42y      |
        | Byte       | BigInteger.valueOf(42)   | 42y      |
        | Byte       | BigDecimal.valueOf(42)   | 42y      |
        | short      | Byte.valueOf("42")       | 42s      |
        | short      | Short.valueOf("42")      | 42s      |
        | short      | Integer.valueOf(42)      | 42s      |
        | short      | Long.valueOf(42l)        | 42s      |
        | short      | Float.valueOf(42f)       | 42s      |
        | short      | Double.valueOf(42d)      | 42s      |
        | short      | BigInteger.valueOf(42)   | 42s      |
        | short      | BigDecimal.valueOf(42)   | 42s      |
        | Short      | Byte.valueOf("42")       | 42s      |
        | Short      | Short.valueOf("42")      | 42s      |
        | Short      | Integer.valueOf(42)      | 42s      |
        | Short      | Long.valueOf(42l)        | 42s      |
        | Short      | Float.valueOf(42f)       | 42s      |
        | Short      | Double.valueOf(42d)      | 42s      |
        | Short      | BigInteger.valueOf(42)   | 42s      |
        | Short      | BigDecimal.valueOf(42)   | 42s      |
        | int        | Byte.valueOf("42")       | 42       |
        | int        | Short.valueOf("42")      | 42       |
        | int        | Integer.valueOf(42)      | 42       |
        | int        | Long.valueOf(42l)        | 42       |
        | int        | Float.valueOf(42f)       | 42       |
        | int        | Double.valueOf(42d)      | 42       |
        | int        | BigInteger.valueOf(42)   | 42       |
        | int        | BigDecimal.valueOf(42)   | 42       |
        | Integer    | Byte.valueOf("42")       | 42       |
        | Integer    | Short.valueOf("42")      | 42       |
        | Integer    | Integer.valueOf(42)      | 42       |
        | Integer    | Long.valueOf(42l)        | 42       |
        | Integer    | Float.valueOf(42f)       | 42       |
        | Integer    | Double.valueOf(42d)      | 42       |
        | Integer    | BigInteger.valueOf(42)   | 42       |
        | Integer    | BigDecimal.valueOf(42)   | 42       |
        | long       | Byte.valueOf("42")       | 42l      |
        | long       | Short.valueOf("42")      | 42l      |
        | long       | Integer.valueOf(42)      | 42l      |
        | long       | Long.valueOf(42l)        | 42l      |
        | long       | Float.valueOf(42f)       | 42l      |
        | long       | Double.valueOf(42d)      | 42l      |
        | long       | BigInteger.valueOf(42)   | 42l      |
        | long       | BigDecimal.valueOf(42)   | 42l      |
        | Long       | Byte.valueOf("42")       | 42l      |
        | Long       | Short.valueOf("42")      | 42l      |
        | Long       | Integer.valueOf(42)      | 42l      |
        | Long       | Long.valueOf(42l)        | 42l      |
        | Long       | Float.valueOf(42f)       | 42l      |
        | Long       | Double.valueOf(42d)      | 42l      |
        | Long       | BigInteger.valueOf(42)   | 42l      |
        | Long       | BigDecimal.valueOf(42)   | 42l      |
        | float      | Byte.valueOf("42")       | 42f      |
        | float      | Short.valueOf("42")      | 42f      |
        | float      | Integer.valueOf(42)      | 42f      |
        | float      | Long.valueOf(42l)        | 42f      |
        | float      | Float.valueOf(123.06f)   | 123.06f  |
        | float      | Double.valueOf(123d)     | 123f     |
        | float      | BigInteger.valueOf(42)   | 42f      |
        | float      | new BigDecimal("123")    | 123f     |
        | Float      | Byte.valueOf("42")       | 42f      |
        | Float      | Short.valueOf("42")      | 42f      |
        | Float      | Integer.valueOf(42)      | 42f      |
        | Float      | Long.valueOf(42l)        | 42f      |
        | Float      | Float.valueOf(123.06f)   | 123.06f  |
        | Float      | Double.valueOf(123d)     | 123f     |
        | Float      | BigInteger.valueOf(42)   | 42f      |
        | Float      | new BigDecimal("123")    | 123f     |
        | double     | Byte.valueOf("42")       | 42d      |
        | double     | Short.valueOf("42")      | 42d      |
        | double     | Integer.valueOf(42)      | 42d      |
        | double     | Long.valueOf(42l)        | 42d      |
        | double     | Float.valueOf(123f)      | 123d     |
        | double     | Double.valueOf(123.06d)  | 123.06d  |
        | double     | BigInteger.valueOf(42)   | 42d      |
        | double     | new BigDecimal("123.06") | 123.06d  |
        | Double     | Byte.valueOf("42")       | 42d      |
        | Double     | Short.valueOf("42")      | 42d      |
        | Double     | Integer.valueOf(42)      | 42d      |
        | Double     | Long.valueOf(42l)        | 42d      |
        | Double     | Float.valueOf(123f)      | 123d     |
        | Double     | Double.valueOf(123.06d)  | 123.06d  |
        | Double     | BigInteger.valueOf(42)   | 42d      |
        | Double     | new BigDecimal("123.06") | 123.06d  |
        | BigInteger | Byte.valueOf("42")       | 42bi     |
        | BigInteger | Short.valueOf("42")      | 42bi     |
        | BigInteger | Integer.valueOf(42)      | 42bi     |
        | BigInteger | Long.valueOf(42l)        | 42bi     |
        | BigInteger | Float.valueOf(123f)      | 123bi    |
        | BigInteger | Double.valueOf(123d)     | 123bi    |
        | BigInteger | BigInteger.valueOf(42)   | 42bi     |
        | BigInteger | new BigDecimal("123")    | 123bi    |
        | BigDecimal | Byte.valueOf("42")       | 42bd     |
        | BigDecimal | Short.valueOf("42")      | 42bd     |
        | BigDecimal | Integer.valueOf(42)      | 42bd     |
        | BigDecimal | Long.valueOf(42l)        | 42bd     |
        | BigDecimal | Float.valueOf(123f)      | 123.0bd  |
        | BigDecimal | Double.valueOf(123.06d)  | 123.06bd |
        | BigDecimal | BigInteger.valueOf(42)   | 42bd     |
        | BigDecimal | new BigDecimal("123.06") | 123.06bd |

    Scenario Outline: throw exception when create bean with property("name", "value") and value is outbound - Number->byte/Byte/short/Short/int/Integer/long/Long/float/Float/double/Double/BigInteger/BigDecimal
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", <input>).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from <errorMessage>/
      """
      When build:
      """
      jFactory.type(Bean.class).properties(new HashMap(){{
        put("value", <input>);
        }}).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from <errorMessage>/
      """
      Examples:
        | type       | input                                                                                                                                                                                                                                                                                                                                   | errorMessage                                    |
        | byte       | Short.valueOf("128")                                                                                                                                                                                                                                                                                                                    | java.lang.Short to byte                         |
        | byte       | Integer.valueOf(128)                                                                                                                                                                                                                                                                                                                    | java.lang.Integer to byte                       |
        | byte       | Long.valueOf(128l)                                                                                                                                                                                                                                                                                                                      | java.lang.Long to byte                          |
        | byte       | Float.valueOf(128f)                                                                                                                                                                                                                                                                                                                     | java.lang.Float to byte                         |
        | byte       | Double.valueOf(128d)                                                                                                                                                                                                                                                                                                                    | java.lang.Double to byte                        |
        | byte       | BigInteger.valueOf(128)                                                                                                                                                                                                                                                                                                                 | java.math.BigInteger to byte                    |
        | byte       | BigDecimal.valueOf(128)                                                                                                                                                                                                                                                                                                                 | java.math.BigDecimal to byte                    |
        | Byte       | Short.valueOf("128")                                                                                                                                                                                                                                                                                                                    | java.lang.Short to class java.lang.Byte         |
        | Byte       | Integer.valueOf(128)                                                                                                                                                                                                                                                                                                                    | java.lang.Integer to class java.lang.Byte       |
        | Byte       | Long.valueOf(128l)                                                                                                                                                                                                                                                                                                                      | java.lang.Long to class java.lang.Byte          |
        | Byte       | Float.valueOf(128f)                                                                                                                                                                                                                                                                                                                     | java.lang.Float to class java.lang.Byte         |
        | Byte       | Double.valueOf(128d)                                                                                                                                                                                                                                                                                                                    | java.lang.Double to class java.lang.Byte        |
        | Byte       | BigInteger.valueOf(128)                                                                                                                                                                                                                                                                                                                 | java.math.BigInteger to class java.lang.Byte    |
        | Byte       | BigDecimal.valueOf(128)                                                                                                                                                                                                                                                                                                                 | java.math.BigDecimal to class java.lang.Byte    |
        | short      | Integer.valueOf(32768)                                                                                                                                                                                                                                                                                                                  | java.lang.Integer to short                      |
        | short      | Long.valueOf(32768l)                                                                                                                                                                                                                                                                                                                    | java.lang.Long to short                         |
        | short      | Float.valueOf(32768f)                                                                                                                                                                                                                                                                                                                   | java.lang.Float to short                        |
        | short      | Double.valueOf(32768d)                                                                                                                                                                                                                                                                                                                  | java.lang.Double to short                       |
        | short      | BigInteger.valueOf(32768)                                                                                                                                                                                                                                                                                                               | java.math.BigInteger to short                   |
        | short      | BigDecimal.valueOf(32768)                                                                                                                                                                                                                                                                                                               | java.math.BigDecimal to short                   |
        | Short      | Integer.valueOf(32768)                                                                                                                                                                                                                                                                                                                  | java.lang.Integer to class java.lang.Short      |
        | Short      | Long.valueOf(32768l)                                                                                                                                                                                                                                                                                                                    | java.lang.Long to class java.lang.Short         |
        | Short      | Float.valueOf(32768f)                                                                                                                                                                                                                                                                                                                   | java.lang.Float to class java.lang.Short        |
        | Short      | Double.valueOf(32768d)                                                                                                                                                                                                                                                                                                                  | java.lang.Double to class java.lang.Short       |
        | Short      | BigInteger.valueOf(32768)                                                                                                                                                                                                                                                                                                               | java.math.BigInteger to class java.lang.Short   |
        | Short      | BigDecimal.valueOf(32768)                                                                                                                                                                                                                                                                                                               | java.math.BigDecimal to class java.lang.Short   |
        | int        | Long.valueOf(2147483648l)                                                                                                                                                                                                                                                                                                               | java.lang.Long to int                           |
        | int        | Float.valueOf(2147483777f)                                                                                                                                                                                                                                                                                                              | java.lang.Float to int                          |
        | int        | Double.valueOf(2147483648d)                                                                                                                                                                                                                                                                                                             | java.lang.Double to int                         |
        | int        | BigInteger.valueOf(2147483648l)                                                                                                                                                                                                                                                                                                         | java.math.BigInteger to int                     |
        | int        | BigDecimal.valueOf(2147483648l)                                                                                                                                                                                                                                                                                                         | java.math.BigDecimal to int                     |
        | Integer    | Long.valueOf(2147483648l)                                                                                                                                                                                                                                                                                                               | java.lang.Long to class java.lang.Integer       |
        | Integer    | Float.valueOf(2147483777f)                                                                                                                                                                                                                                                                                                              | java.lang.Float to class java.lang.Integer      |
        | Integer    | Double.valueOf(2147483648d)                                                                                                                                                                                                                                                                                                             | java.lang.Double to class java.lang.Integer     |
        | Integer    | BigInteger.valueOf(2147483648l)                                                                                                                                                                                                                                                                                                         | java.math.BigInteger to class java.lang.Integer |
        | Integer    | BigDecimal.valueOf(2147483648l)                                                                                                                                                                                                                                                                                                         | java.math.BigDecimal to class java.lang.Integer |
        | long       | Float.valueOf(9223373136366400000f)                                                                                                                                                                                                                                                                                                     | java.lang.Float to long                         |
        | long       | Double.valueOf(9223372036854776833d)                                                                                                                                                                                                                                                                                                    | java.lang.Double to long                        |
        | long       | new BigInteger("9223372036854775808")                                                                                                                                                                                                                                                                                                   | java.math.BigInteger to long                    |
        | long       | new BigDecimal("9223372036854775808")                                                                                                                                                                                                                                                                                                   | java.math.BigDecimal to long                    |
        | Long       | Float.valueOf(9223373136366400000f)                                                                                                                                                                                                                                                                                                     | java.lang.Float to class java.lang.Long         |
        | Long       | Double.valueOf(9223372036854776833d)                                                                                                                                                                                                                                                                                                    | java.lang.Double to class java.lang.Long        |
        | Long       | new BigInteger("9223372036854775808")                                                                                                                                                                                                                                                                                                   | java.math.BigInteger to class java.lang.Long    |
        | Long       | new BigDecimal("9223372036854775808")                                                                                                                                                                                                                                                                                                   | java.math.BigDecimal to class java.lang.Long    |
        | float      | Double.valueOf(3.4028236E39d)                                                                                                                                                                                                                                                                                                           | java.lang.Double to float                       |
        | float      | new BigInteger("340282366920938463463374607431768211456")                                                                                                                                                                                                                                                                               | java.math.BigInteger to float                   |
        | float      | new BigDecimal("340282366920938463463374607431768211456")                                                                                                                                                                                                                                                                               | java.math.BigDecimal to float                   |
        | Float      | Double.valueOf(3.4028236E39d)                                                                                                                                                                                                                                                                                                           | java.lang.Double to class java.lang.Float       |
        | Float      | new BigInteger("340282366920938463463374607431768211456")                                                                                                                                                                                                                                                                               | java.math.BigInteger to class java.lang.Float   |
        | Float      | new BigDecimal("340282366920938463463374607431768211456")                                                                                                                                                                                                                                                                               | java.math.BigDecimal to class java.lang.Float   |
        | double     | new BigInteger("179769313486231570814527423731704356798070567525844996598917476803157260780028538760589558632766878171540458953514382464234321326889464182768467546703537516986049910576551282076245490090389328944075868508455133942304583236903222948165808559332123348274797826204144723168738177180919299881250404026184124858368") | java.math.BigInteger to double                  |
        | double     | new BigDecimal("179769313486231570814527423731704356798070567525844996598917476803157260780028538760589558632766878171540458953514382464234321326889464182768467546703537516986049910576551282076245490090389328944075868508455133942304583236903222948165808559332123348274797826204144723168738177180919299881250404026184124858368") | java.math.BigDecimal to double                  |
        | Double     | new BigInteger("179769313486231570814527423731704356798070567525844996598917476803157260780028538760589558632766878171540458953514382464234321326889464182768467546703537516986049910576551282076245490090389328944075868508455133942304583236903222948165808559332123348274797826204144723168738177180919299881250404026184124858368") | java.math.BigInteger to class java.lang.Double  |
        | Double     | new BigDecimal("179769313486231570814527423731704356798070567525844996598917476803157260780028538760589558632766878171540458953514382464234321326889464182768467546703537516986049910576551282076245490090389328944075868508455133942304583236903222948165808559332123348274797826204144723168738177180919299881250404026184124858368") | java.math.BigDecimal to class java.lang.Double  |
        | BigInteger | Float.POSITIVE_INFINITY                                                                                                                                                                                                                                                                                                                 | java.lang.Float to class java.math.BigInteger   |
        | BigInteger | Float.NEGATIVE_INFINITY                                                                                                                                                                                                                                                                                                                 | java.lang.Float to class java.math.BigInteger   |
        | BigInteger | Float.NaN                                                                                                                                                                                                                                                                                                                               | java.lang.Float to class java.math.BigInteger   |
        | BigInteger | Double.POSITIVE_INFINITY                                                                                                                                                                                                                                                                                                                | java.lang.Double to class java.math.BigInteger  |
        | BigInteger | Double.NEGATIVE_INFINITY                                                                                                                                                                                                                                                                                                                | java.lang.Double to class java.math.BigInteger  |
        | BigInteger | Double.NaN                                                                                                                                                                                                                                                                                                                              | java.lang.Double to class java.math.BigInteger  |
        | BigDecimal | Float.POSITIVE_INFINITY                                                                                                                                                                                                                                                                                                                 | java.lang.Float to class java.math.BigDecimal   |
        | BigDecimal | Float.NEGATIVE_INFINITY                                                                                                                                                                                                                                                                                                                 | java.lang.Float to class java.math.BigDecimal   |
        | BigDecimal | Float.NaN                                                                                                                                                                                                                                                                                                                               | java.lang.Float to class java.math.BigDecimal   |
        | BigDecimal | Double.POSITIVE_INFINITY                                                                                                                                                                                                                                                                                                                | java.lang.Double to class java.math.BigDecimal  |
        | BigDecimal | Double.NEGATIVE_INFINITY                                                                                                                                                                                                                                                                                                                | java.lang.Double to class java.math.BigDecimal  |
        | BigDecimal | Double.NaN                                                                                                                                                                                                                                                                                                                              | java.lang.Double to class java.math.BigDecimal  |

    Scenario Outline: throw exception when create bean with property("name", "value") and value is a custom Number implementation - Number->byte/Byte/short/Short/int/Integer
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      Given the following bean class:
      """
      public class NumberToBeConverted extends NumberBase {
        public int intValue() {
          return 42;
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", new NumberToBeConverted()).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to <typeInMessage>/
      """
      When build:
      """
      jFactory.type(Bean.class).properties(new HashMap(){{
        put("value", new NumberToBeConverted());
        }}).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to <typeInMessage>/
      """
      Examples:
        | type    | typeInMessage           |
        | byte    | byte                    |
        | Byte    | class java.lang.Byte    |
        | short   | short                   |
        | Short   | class java.lang.Short   |
        | int     | int                     |
        | Integer | class java.lang.Integer |

    Scenario Outline: throw exception when create bean with property("name", "value") and value is a custom Number implementation - Number->long/Long
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      Given the following bean class:
      """
      public class NumberToBeConverted extends NumberBase {
        public long longValue() {
          return 42l;
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", new NumberToBeConverted()).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to <typeInMessage>/
      """
      When build:
      """
      jFactory.type(Bean.class).properties(new HashMap(){{
        put("value", new NumberToBeConverted());
        }}).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to <typeInMessage>/
      """
      Examples:
        | type | typeInMessage        |
        | long | long                 |
        | Long | class java.lang.Long |

    Scenario Outline: throw exception when create bean with property("name", "value") and value is a custom Number implementation - Number->float/Float
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      Given the following bean class:
      """
      public class NumberToBeConverted extends NumberBase {
        public float floatValue() {
          return 123.06f;
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", new NumberToBeConverted()).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to <typeInMessage>/
      """
      When build:
      """
      jFactory.type(Bean.class).properties(new HashMap(){{
        put("value", new NumberToBeConverted());
        }}).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to <typeInMessage>/
      """
      Examples:
        | type  | typeInMessage         |
        | float | float                 |
        | Float | class java.lang.Float |

    Scenario Outline: throw exception when create bean with property("name", "value") and value is a custom Number implementation - Number->double/Double
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      Given the following bean class:
      """
      public class NumberToBeConverted extends NumberBase {
        public double doubleValue() {
          return 123.06d;
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", new NumberToBeConverted()).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to <typeInMessage>/
      """
      When build:
      """
      jFactory.type(Bean.class).properties(new HashMap(){{
        put("value", new NumberToBeConverted());
        }}).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to <typeInMessage>/
      """
      Examples:
        | type   | typeInMessage          |
        | double | double                 |
        | Double | class java.lang.Double |

    Scenario: throw exception create bean with property("name", "value") and value is a custom Number implementation - Number->BigInteger
      Given the following bean class:
      """
      public class Bean {
        public BigInteger value;
      }
      """
      Given the following bean class:
      """
      public class NumberToBeConverted extends NumberBase {
        public int intValue() {
          return 42;
        }
        public long longValue() {
          return 42l;
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", new NumberToBeConverted()).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to class java.math.BigInteger/
      """
      When build:
      """
      jFactory.type(Bean.class).properties(new HashMap(){{
        put("value", new NumberToBeConverted());
        }}).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to class java.math.BigInteger/
      """

    Scenario: throw exception create bean with property("name", "value") and value is a custom Number implementation - Number->BigDecimal
      Given the following bean class:
      """
      public class Bean {
        public BigDecimal value;
      }
      """
      Given the following bean class:
      """
      public class NumberToBeConverted extends NumberBase {
        public float floatValue() {
          return 123.06f;
        }
        public double doubleValue() {
          return 123.06d;
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("value", new NumberToBeConverted()).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to class java.math.BigDecimal/
      """
      When build:
      """
      jFactory.type(Bean.class).properties(new HashMap(){{
        put("value", new NumberToBeConverted());
        }}).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to class java.math.BigDecimal/
      """

  Rule: Create bean with propertyValue("name", PropertyValue)

    Background:
      And the following bean class:
      """
      public class PropertyValueImpl<T> implements PropertyValue {
        private final T value;

        public PropertyValueImpl(T value) {
          this.value = value;
        }

        public <T> Builder<T> setToBuilder(String property, Builder<T> builder) {
          return builder.property(property, value);
        }
      }
      """
      And the following bean class:
      """
      public class PropertyValueImplConverted<T, C> implements PropertyValue {
        private final C value;

        public PropertyValueImplConverted(C value) {
          this.value = value;
        }

        public <T> Builder<T> setToBuilder(String property, Builder<T> builder) {
          return builder.property(property, value);
        }
      }
      """

    Scenario Outline: Create bean with propertyValue("name", PropertyValue) - All base type = except special cases
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImpl<<type>>(<input>)).create();
      """
      Then the result should:
      """
      value= <expected>
      """
      Examples:
        | type       | input                    | expected    |
        | String     | "input-value"            | input-value |
        | Integer    | new Integer(42)          | 42          |
        | Short      | Short.valueOf("42")      | 42s         |
        | Byte       | Byte.valueOf("42")       | 42y         |
        | Long       | new Long(42l)            | 42l         |
        | Float      | new Float(123.06f)       | 123.06f     |
        | Double     | new Double(123.06d)      | 123.06d     |
        | Boolean    | true                     | true        |
        | BigInteger | BigInteger.valueOf(42)   | 42bi        |
        | BigDecimal | new BigDecimal("123.06") | 123.06bd    |

    Scenario Outline: Create bean with propertyValue("name", PropertyValue) String value converter - All base type = except special cases
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImplConverted<<type>, String>(<input>)).create();
      """
      Then the result should:
      """
      value= <expected>
      """
      Examples:
        | type       | input    | expected |
        | Integer    | "42"     | 42       |
        | Short      | "42"     | 42s      |
        | Byte       | "42"     | 42y      |
        | Long       | "42"     | 42l      |
        | Float      | "123.06" | 123.06f  |
        | Double     | "123.06" | 123.06d  |
        | Boolean    | "true"   | true     |
        | BigInteger | "42"     | 42bi     |
        | BigDecimal | "123.06" | 123.06bd |

    Scenario Outline: Create bean with propertyValue("name", PropertyValue) - All base type : except special cases
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImpl<<type>>(<input>)).create();
      """
      Then the result should:
      """
      value: <expected>
      """
      Examples:
        | type                     | input                                                                            | expected                               |
        | UUID                     | UUID.fromString("123e4567-e89b-12d3-a456-426655440000")                          | '123e4567-e89b-12d3-a456-426655440000' |
        | java.time.Instant        | java.time.Instant.EPOCH                                                          | '1970-01-01T00:00:00Z'                 |
        | java.time.LocalDate      | java.time.LocalDate.of(1978, 5, 3)                                               | '1978-05-03'                           |
        | java.time.LocalTime      | java.time.LocalTime.of(12, 30, 45)                                               | '12:30:45'                             |
        | java.time.LocalDateTime  | java.time.LocalDateTime.of(1978, 5, 3, 12, 30, 45)                               | '1978-05-03T12:30:45'                  |
        | java.time.OffsetDateTime | java.time.OffsetDateTime.of(1978, 5, 3, 12, 30, 45, 0, java.time.ZoneOffset.UTC) | '1978-05-03T12:30:45Z'                 |
        | java.time.ZonedDateTime  | java.time.ZonedDateTime.of(1978, 5, 3, 12, 30, 45, 0, java.time.ZoneOffset.UTC)  | '1978-05-03T12:30:45Z'                 |

    Scenario Outline: Create bean with propertyValue("name", PropertyValue) String value converter - All base type : except special cases
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImplConverted<<type>, String>(<input>)).create();
      """
      Then the result should:
      """
      value: <expected>
      """
      Examples:
        | type                     | input                                  | expected                               |
        | UUID                     | "123e4567-e89b-12d3-a456-426655440000" | '123e4567-e89b-12d3-a456-426655440000' |
        | java.time.Instant        | "2009-07-24T02:03:04Z"                 | '2009-07-24T02:03:04Z'                 |
        | java.time.LocalDate      | "1978-05-03"                           | '1978-05-03'                           |
        | java.time.LocalTime      | "12:30:45"                             | '12:30:45'                             |
        | java.time.LocalDateTime  | "1978-05-03T12:30:45"                  | '1978-05-03T12:30:45'                  |
        | java.time.OffsetDateTime | "1978-05-03T12:30:45Z"                 | '1978-05-03T12:30:45Z'                 |
        | java.time.ZonedDateTime  | "1978-05-03T12:30:45Z"                 | '1978-05-03T12:30:45Z'                 |
        | java.time.YearMonth      | "1978-05"                              | '1978-05'                              |

    Scenario Outline: Create bean with propertyValue("name", PropertyValue) - custom type
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImpl<<type>>(<input>)).create();
      """
      Then the result should:
      """
      value: <expected>
      """
      Examples:
        | type                | input                           | expected              |
        | java.time.YearMonth | java.time.YearMonth.of(1978, 5) | '1978-05'             |
        | CustomObject        | new CustomObject()              | { anyProperty= null } |

    Scenario Outline: Create bean with propertyValue("name", PropertyValue) String value converter - custom type
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImplConverted<<type>, String>(<input>)).create();
      """
      Then the result should:
      """
      value: <expected>
      """
      Examples:
        | type                | input     | expected  |
        | java.time.YearMonth | "1978-05" | '1978-05' |

    Scenario Outline: Create bean with propertyValue("name", PropertyValue) - Date
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImpl<<type>>(<input>)).create();
      """
      Then the result should:
      """
      value.toInstant: <expected>
      """
      Examples:
        | type | input       | expected               |
        | Date | new Date(0) | '1970-01-01T00:00:00Z' |

    Scenario Outline: Create bean with propertyValue("name", PropertyValue) String value converter - Date
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImplConverted<<type>, String>(<input>)).create();
      """
      Then the result should:
      """
      value.toInstant: <expected>
      """
      Examples:
        | type | input        | expected               |
        | Date | "1978-05-03" | '1978-05-03T00:00:00Z' |

    Scenario: Create bean with propertyValue("name", PropertyValue) - Object->String
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      Given the following bean class:
      """
      public class ToBeConverted {
        public String toString() {
          return "converted-value";
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImplConverted<String, ToBeConverted>(new ToBeConverted())).create();
      """
      Then the result should:
      """
      value= converted-value
      """

    Scenario Outline: create bean with propertyValue("name", PropertyValue) - Number->byte/Byte/short/Short/int/Integer/long/Long/float/Float/double/Double/BigInteger/BigDecimal
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImplConverted<<type>, <convertedType>>(<input>)).create();
      """
      Then the result should:
      """
      value= <expected>
      """
      Examples:
        | type       | convertedType | input                    | expected |
        | Byte       | Byte          | Byte.valueOf("42")       | 42y      |
        | Byte       | Short         | Short.valueOf("42")      | 42y      |
        | Byte       | Integer       | Integer.valueOf(42)      | 42y      |
        | Byte       | Long          | Long.valueOf(42l)        | 42y      |
        | Byte       | Float         | Float.valueOf(42f)       | 42y      |
        | Byte       | Double        | Double.valueOf(42d)      | 42y      |
        | Byte       | BigInteger    | BigInteger.valueOf(42)   | 42y      |
        | Byte       | BigDecimal    | BigDecimal.valueOf(42)   | 42y      |
        | Short      | Byte          | Byte.valueOf("42")       | 42s      |
        | Short      | Short         | Short.valueOf("42")      | 42s      |
        | Short      | Integer       | Integer.valueOf(42)      | 42s      |
        | Short      | Long          | Long.valueOf(42l)        | 42s      |
        | Short      | Float         | Float.valueOf(42f)       | 42s      |
        | Short      | Double        | Double.valueOf(42d)      | 42s      |
        | Short      | BigInteger    | BigInteger.valueOf(42)   | 42s      |
        | Short      | BigDecimal    | BigDecimal.valueOf(42)   | 42s      |
        | Integer    | Byte          | Byte.valueOf("42")       | 42       |
        | Integer    | Short         | Short.valueOf("42")      | 42       |
        | Integer    | Integer       | Integer.valueOf(42)      | 42       |
        | Integer    | Long          | Long.valueOf(42l)        | 42       |
        | Integer    | Float         | Float.valueOf(42f)       | 42       |
        | Integer    | Double        | Double.valueOf(42d)      | 42       |
        | Integer    | BigInteger    | BigInteger.valueOf(42)   | 42       |
        | Integer    | BigDecimal    | BigDecimal.valueOf(42)   | 42       |
        | Long       | Byte          | Byte.valueOf("42")       | 42l      |
        | Long       | Short         | Short.valueOf("42")      | 42l      |
        | Long       | Integer       | Integer.valueOf(42)      | 42l      |
        | Long       | Long          | Long.valueOf(42l)        | 42l      |
        | Long       | Float         | Float.valueOf(42f)       | 42l      |
        | Long       | Double        | Double.valueOf(42d)      | 42l      |
        | Long       | BigInteger    | BigInteger.valueOf(42)   | 42l      |
        | Long       | BigDecimal    | BigDecimal.valueOf(42)   | 42l      |
        | Float      | Byte          | Byte.valueOf("42")       | 42f      |
        | Float      | Short         | Short.valueOf("42")      | 42f      |
        | Float      | Integer       | Integer.valueOf(42)      | 42f      |
        | Float      | Long          | Long.valueOf(42l)        | 42f      |
        | Float      | Float         | Float.valueOf(123.06f)   | 123.06f  |
        | Float      | Double        | Double.valueOf(123d)     | 123f     |
        | Float      | BigInteger    | BigInteger.valueOf(42)   | 42f      |
        | Float      | BigDecimal    | new BigDecimal("123")    | 123f     |
        | Double     | Byte          | Byte.valueOf("42")       | 42d      |
        | Double     | Short         | Short.valueOf("42")      | 42d      |
        | Double     | Integer       | Integer.valueOf(42)      | 42d      |
        | Double     | Long          | Long.valueOf(42l)        | 42d      |
        | Double     | Float         | Float.valueOf(123f)      | 123d     |
        | Double     | Double        | Double.valueOf(123.06d)  | 123.06d  |
        | Double     | BigInteger    | BigInteger.valueOf(42)   | 42d      |
        | Double     | BigDecimal    | new BigDecimal("123.06") | 123.06d  |
        | BigInteger | Byte          | Byte.valueOf("42")       | 42bi     |
        | BigInteger | Short         | Short.valueOf("42")      | 42bi     |
        | BigInteger | Integer       | Integer.valueOf(42)      | 42bi     |
        | BigInteger | Long          | Long.valueOf(42l)        | 42bi     |
        | BigInteger | Float         | Float.valueOf(123f)      | 123bi    |
        | BigInteger | Double        | Double.valueOf(123d)     | 123bi    |
        | BigInteger | BigInteger    | BigInteger.valueOf(42)   | 42bi     |
        | BigInteger | BigDecimal    | new BigDecimal("123")    | 123bi    |
        | BigDecimal | Byte          | Byte.valueOf("42")       | 42bd     |
        | BigDecimal | Short         | Short.valueOf("42")      | 42bd     |
        | BigDecimal | Integer       | Integer.valueOf(42)      | 42bd     |
        | BigDecimal | Long          | Long.valueOf(42l)        | 42bd     |
        | BigDecimal | Float         | Float.valueOf(123f)      | 123.0bd  |
        | BigDecimal | Double        | Double.valueOf(123.06d)  | 123.06bd |
        | BigDecimal | BigInteger    | BigInteger.valueOf(42)   | 42bd     |
        | BigDecimal | BigDecimal    | new BigDecimal("123.06") | 123.06bd |

    Scenario Outline: throw exception when create bean with propertyValue("name", PropertyValue) and value is outbound - Number->byte/Byte/short/Short/int/Integer/long/Long/float/Float/double/Double/BigInteger/BigDecimal
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImplConverted<<type>, <convertedType>>(<input>)).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from <errorMessage>/
      """
      Examples:
        | type       | convertedType | input                                                                                                                                                                                                                                                                                                                                   | errorMessage                                    |
        | Byte       | Short         | Short.valueOf("128")                                                                                                                                                                                                                                                                                                                    | java.lang.Short to class java.lang.Byte         |
        | Byte       | Integer       | Integer.valueOf(128)                                                                                                                                                                                                                                                                                                                    | java.lang.Integer to class java.lang.Byte       |
        | Byte       | Long          | Long.valueOf(128l)                                                                                                                                                                                                                                                                                                                      | java.lang.Long to class java.lang.Byte          |
        | Byte       | Float         | Float.valueOf(128f)                                                                                                                                                                                                                                                                                                                     | java.lang.Float to class java.lang.Byte         |
        | Byte       | Double        | Double.valueOf(128d)                                                                                                                                                                                                                                                                                                                    | java.lang.Double to class java.lang.Byte        |
        | Byte       | BigInteger    | BigInteger.valueOf(128)                                                                                                                                                                                                                                                                                                                 | java.math.BigInteger to class java.lang.Byte    |
        | Byte       | BigDecimal    | BigDecimal.valueOf(128)                                                                                                                                                                                                                                                                                                                 | java.math.BigDecimal to class java.lang.Byte    |
        | Short      | Integer       | Integer.valueOf(32768)                                                                                                                                                                                                                                                                                                                  | java.lang.Integer to class java.lang.Short      |
        | Short      | Long          | Long.valueOf(32768l)                                                                                                                                                                                                                                                                                                                    | java.lang.Long to class java.lang.Short         |
        | Short      | Float         | Float.valueOf(32768f)                                                                                                                                                                                                                                                                                                                   | java.lang.Float to class java.lang.Short        |
        | Short      | Double        | Double.valueOf(32768d)                                                                                                                                                                                                                                                                                                                  | java.lang.Double to class java.lang.Short       |
        | Short      | BigInteger    | BigInteger.valueOf(32768)                                                                                                                                                                                                                                                                                                               | java.math.BigInteger to class java.lang.Short   |
        | Short      | BigDecimal    | BigDecimal.valueOf(32768)                                                                                                                                                                                                                                                                                                               | java.math.BigDecimal to class java.lang.Short   |
        | Integer    | Long          | Long.valueOf(2147483648l)                                                                                                                                                                                                                                                                                                               | java.lang.Long to class java.lang.Integer       |
        | Integer    | Float         | Float.valueOf(2147483777f)                                                                                                                                                                                                                                                                                                              | java.lang.Float to class java.lang.Integer      |
        | Integer    | Double        | Double.valueOf(2147483648d)                                                                                                                                                                                                                                                                                                             | java.lang.Double to class java.lang.Integer     |
        | Integer    | BigInteger    | BigInteger.valueOf(2147483648l)                                                                                                                                                                                                                                                                                                         | java.math.BigInteger to class java.lang.Integer |
        | Integer    | BigDecimal    | BigDecimal.valueOf(2147483648l)                                                                                                                                                                                                                                                                                                         | java.math.BigDecimal to class java.lang.Integer |
        | Long       | Float         | Float.valueOf(9223373136366400000f)                                                                                                                                                                                                                                                                                                     | java.lang.Float to class java.lang.Long         |
        | Long       | Double        | Double.valueOf(9223372036854776833d)                                                                                                                                                                                                                                                                                                    | java.lang.Double to class java.lang.Long        |
        | Long       | BigInteger    | new BigInteger("9223372036854775808")                                                                                                                                                                                                                                                                                                   | java.math.BigInteger to class java.lang.Long    |
        | Long       | BigDecimal    | new BigDecimal("9223372036854775808")                                                                                                                                                                                                                                                                                                   | java.math.BigDecimal to class java.lang.Long    |
        | Float      | Double        | Double.valueOf(3.4028236E39d)                                                                                                                                                                                                                                                                                                           | java.lang.Double to class java.lang.Float       |
        | Float      | BigInteger    | new BigInteger("340282366920938463463374607431768211456")                                                                                                                                                                                                                                                                               | java.math.BigInteger to class java.lang.Float   |
        | Float      | BigDecimal    | new BigDecimal("340282366920938463463374607431768211456")                                                                                                                                                                                                                                                                               | java.math.BigDecimal to class java.lang.Float   |
        | Double     | BigInteger    | new BigInteger("179769313486231570814527423731704356798070567525844996598917476803157260780028538760589558632766878171540458953514382464234321326889464182768467546703537516986049910576551282076245490090389328944075868508455133942304583236903222948165808559332123348274797826204144723168738177180919299881250404026184124858368") | java.math.BigInteger to class java.lang.Double  |
        | Double     | BigDecimal    | new BigDecimal("179769313486231570814527423731704356798070567525844996598917476803157260780028538760589558632766878171540458953514382464234321326889464182768467546703537516986049910576551282076245490090389328944075868508455133942304583236903222948165808559332123348274797826204144723168738177180919299881250404026184124858368") | java.math.BigDecimal to class java.lang.Double  |
        | BigInteger | Float         | Float.POSITIVE_INFINITY                                                                                                                                                                                                                                                                                                                 | java.lang.Float to class java.math.BigInteger   |
        | BigInteger | Float         | Float.NEGATIVE_INFINITY                                                                                                                                                                                                                                                                                                                 | java.lang.Float to class java.math.BigInteger   |
        | BigInteger | Float         | Float.NaN                                                                                                                                                                                                                                                                                                                               | java.lang.Float to class java.math.BigInteger   |
        | BigInteger | Double        | Double.POSITIVE_INFINITY                                                                                                                                                                                                                                                                                                                | java.lang.Double to class java.math.BigInteger  |
        | BigInteger | Double        | Double.NEGATIVE_INFINITY                                                                                                                                                                                                                                                                                                                | java.lang.Double to class java.math.BigInteger  |
        | BigInteger | Double        | Double.NaN                                                                                                                                                                                                                                                                                                                              | java.lang.Double to class java.math.BigInteger  |
        | BigDecimal | Float         | Float.POSITIVE_INFINITY                                                                                                                                                                                                                                                                                                                 | java.lang.Float to class java.math.BigDecimal   |
        | BigDecimal | Float         | Float.NEGATIVE_INFINITY                                                                                                                                                                                                                                                                                                                 | java.lang.Float to class java.math.BigDecimal   |
        | BigDecimal | Float         | Float.NaN                                                                                                                                                                                                                                                                                                                               | java.lang.Float to class java.math.BigDecimal   |
        | BigDecimal | Double        | Double.POSITIVE_INFINITY                                                                                                                                                                                                                                                                                                                | java.lang.Double to class java.math.BigDecimal  |
        | BigDecimal | Double        | Double.NEGATIVE_INFINITY                                                                                                                                                                                                                                                                                                                | java.lang.Double to class java.math.BigDecimal  |
        | BigDecimal | Double        | Double.NaN                                                                                                                                                                                                                                                                                                                              | java.lang.Double to class java.math.BigDecimal  |

    Scenario Outline: throw exception when create bean with propertyValue("name", PropertyValue) and value is a custom Number implementation - Number->byte/Byte/short/Short/int/Integer
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      Given the following bean class:
      """
      public class NumberToBeConverted extends NumberBase {
        public int intValue() {
          return 42;
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImplConverted<<type>, NumberToBeConverted>(new NumberToBeConverted())).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to <typeInMessage>/
      """
      Examples:
        | type    | typeInMessage           |
        | Byte    | class java.lang.Byte    |
        | Short   | class java.lang.Short   |
        | Integer | class java.lang.Integer |

    Scenario Outline: throw exception when create bean with propertyValue("name", PropertyValue) and value is a custom Number implementation - Number->long/Long
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      Given the following bean class:
      """
      public class NumberToBeConverted extends NumberBase {
        public long longValue() {
          return 42l;
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImplConverted<<type>, NumberToBeConverted>(new NumberToBeConverted())).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to <typeInMessage>/
      """
      Examples:
        | type | typeInMessage        |
        | Long | class java.lang.Long |

    Scenario Outline: throw exception when create bean with propertyValue("name", PropertyValue) and value is a custom Number implementation - Number->float/Float
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      Given the following bean class:
      """
      public class NumberToBeConverted extends NumberBase {
        public float floatValue() {
          return 123.06f;
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImplConverted<<type>, NumberToBeConverted>(new NumberToBeConverted())).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to <typeInMessage>/
      """
      Examples:
        | type  | typeInMessage         |
        | Float | class java.lang.Float |

    Scenario Outline: throw exception when create bean with propertyValue("name", PropertyValue) and value is a custom Number implementation - Number->double/Double
      Given the following bean class:
      """
      public class Bean {
        public <type> value;
      }
      """
      Given the following bean class:
      """
      public class NumberToBeConverted extends NumberBase {
        public double doubleValue() {
          return 123.06d;
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImplConverted<<type>, NumberToBeConverted>(new NumberToBeConverted())).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to <typeInMessage>/
      """
      Examples:
        | type   | typeInMessage          |
        | Double | class java.lang.Double |

    Scenario: throw exception create bean with propertyValue("name", PropertyValue) and value is a custom Number implementation - Number->BigInteger
      Given the following bean class:
      """
      public class Bean {
        public BigInteger value;
      }
      """
      Given the following bean class:
      """
      public class NumberToBeConverted extends NumberBase {
        public int intValue() {
          return 42;
        }
        public long longValue() {
          return 42l;
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImplConverted<BigInteger, NumberToBeConverted>(new NumberToBeConverted())).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to class java.math.BigInteger/
      """

    Scenario: throw exception create bean with propertyValue("name", PropertyValue) and value is a custom Number implementation - Number->BigDecimal
      Given the following bean class:
      """
      public class Bean {
        public BigDecimal value;
      }
      """
      Given the following bean class:
      """
      public class NumberToBeConverted extends NumberBase {
        public float floatValue() {
          return 123.06f;
        }
        public double doubleValue() {
          return 123.06d;
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).propertyValue("value", new PropertyValueImplConverted<BigDecimal, NumberToBeConverted>(new NumberToBeConverted())).create();
      """
      Then should raise error:
      """
      message= /Cannot convert from .+NumberToBeConverted to class java.math.BigDecimal/
      """
