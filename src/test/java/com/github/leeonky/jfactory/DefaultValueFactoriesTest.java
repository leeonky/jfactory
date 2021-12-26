package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.PropertyWriter;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.Date;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultValueFactoriesTest {

    @Test
    void default_string() {
        assertValue(String.class, 1, "str", "str#1");
        assertValue(String.class, 3, "name", "name#3");
    }

    @Test
    void default_int() {
        assertValue(int.class, 1, 1);
        assertValue(Integer.class, 3, 3);
    }

    @Test
    void default_short() {
        assertValue(short.class, 1, (short) 1);
        assertValue(Short.class, 2, (short) 2);
    }

    @Test
    void default_byte() {
        assertValue(byte.class, 1, (byte) 1);
        assertValue(Byte.class, 2, (byte) 2);
    }

    @Test
    void default_long() {
        assertValue(long.class, 1, 1L);
        assertValue(Long.class, 2, 2L);
    }

    @Test
    void default_float() {
        assertValue(float.class, 1, 1.0f);
        assertValue(Float.class, 2, 2.0f);
    }

    @Test
    void default_double() {
        assertValue(double.class, 1, 1.0);
        assertValue(Double.class, 2, 2.0);
    }

    @Test
    void should_raise_error_when_invalid_generic_args() {
        assertThrows(IllegalStateException.class, () -> new InvalidGenericArgDefaultValueFactory<>().getType());
    }

    @Test
    void default_boolean() {
        assertValue(boolean.class, 1, true);
        assertValue(Boolean.class, 2, false);
        assertValue(boolean.class, 3, true);
    }

    @Test
    void default_big_int() {
        assertValue(BigInteger.class, 1, BigInteger.valueOf(1));
        assertValue(BigInteger.class, 2, BigInteger.valueOf(2));
    }

    @Test
    void default_big_decimal() {
        assertValue(BigDecimal.class, 1, BigDecimal.valueOf(1));
        assertValue(BigDecimal.class, 2, BigDecimal.valueOf(2));
    }

    @Test
    void default_uuid() {
        assertValue(UUID.class, 1, UUID.fromString("00000000-0000-0000-0000-000000000001"));
        assertValue(UUID.class, 2, UUID.fromString("00000000-0000-0000-0000-000000000002"));
    }

    @Test
    void default_instant() {
        assertValue(Instant.class, 1, Instant.parse("1996-01-23T00:00:01Z"));
        assertValue(Instant.class, 2, Instant.parse("1996-01-23T00:00:02Z"));
    }

    @Test
    void default_date() {
        assertValue(Date.class, 1, Date.from(Instant.parse("1996-01-24T00:00:00Z")));
        assertValue(Date.class, 2, Date.from(Instant.parse("1996-01-25T00:00:00Z")));
    }

    @Test
    void default_local_time() {
        assertValue(LocalTime.class, 1, LocalTime.parse("00:00:01"));
        assertValue(LocalTime.class, 2, LocalTime.parse("00:00:02"));
    }

    @Test
    void default_local_date() {
        assertValue(LocalDate.class, 1, LocalDate.parse("1996-01-24"));
        assertValue(LocalDate.class, 2, LocalDate.parse("1996-01-25"));
    }

    @Test
    void default_local_date_time() {
        assertValue(LocalDateTime.class, 1, LocalDateTime.parse("1996-01-23T00:00:01"));
        assertValue(LocalDateTime.class, 2, LocalDateTime.parse("1996-01-23T00:00:02"));
    }

    @Test
    void default_offset_date_time() {
        assertValue(OffsetDateTime.class, 1, Instant.parse("1996-01-23T00:00:01Z").atZone(ZoneId.systemDefault()).toOffsetDateTime());
        assertValue(OffsetDateTime.class, 2, Instant.parse("1996-01-23T00:00:02Z").atZone(ZoneId.systemDefault()).toOffsetDateTime());
    }

    @Test
    void default_zoned_date_time() {
        assertValue(ZonedDateTime.class, 1, Instant.parse("1996-01-23T00:00:01Z").atZone(ZoneId.systemDefault()));
        assertValue(ZonedDateTime.class, 2, Instant.parse("1996-01-23T00:00:02Z").atZone(ZoneId.systemDefault()));
    }

    private void assertValue(Class<?> type, int sequence, String property, Object expected) {
        assertThat(new DefaultValueFactories().query(type).get()
                .create(null, new RootInstance<>(sequence, new Spec<>(), new DefaultArguments())
                        .sub(stubPropertyWriter(property)))).isEqualTo(expected);
    }

    private PropertyWriter<?> stubPropertyWriter(String property) {
        return new PropertyWriter<Object>() {
            @Override
            public BiConsumer<Object, Object> setter() {
                return null;
            }

            @Override
            public void setValue(Object bean, Object value) {
            }

            @Override
            public String getName() {
                return property;
            }

            @Override
            public Object tryConvert(Object value) {
                return null;
            }

            @Override
            public BeanClass<Object> getBeanType() {
                return null;
            }

            @Override
            public BeanClass<?> getType() {
                return null;
            }

            @Override
            public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
                return null;
            }
        };
    }

    private void assertValue(Class<?> type, int sequence, Object expected) {
        assertThat(new DefaultValueFactories().query(type).get()
                .create(null, new RootInstance<>(sequence, new Spec<>(), new DefaultArguments())
                        .sub(stubPropertyWriter(null)))).isEqualTo(expected);
    }

    @Test
    void default_value_builder_create_default_value() {
        assertThat(new DefaultValueFactories.DefaultTypeFactory<>(BeanClass.create(int.class)).create(null, null))
                .isInstanceOf(Integer.class);
    }

    public static class InvalidGenericArgDefaultValueFactory<V> implements DefaultValueFactory<V> {
        @Override
        public <T> V create(BeanClass<T> beanType, SubInstance<T> instance) {
            return null;
        }
    }
}