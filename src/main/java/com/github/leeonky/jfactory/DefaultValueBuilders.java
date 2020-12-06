package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.Optional.ofNullable;

class DefaultValueBuilders {
    private static final LocalDate LOCAL_DATE_START = LocalDate.parse("1996-01-23");
    private static final LocalDateTime LOCAL_DATE_TIME_START = LocalDateTime.parse("1996-01-23T00:00:00");
    private static final LocalTime LOCAL_TIME_START = LocalTime.parse("00:00:00");
    private static final Instant INSTANT_START = Instant.parse("1996-01-23T00:00:00Z");
    private final Map<Class<?>, DefaultValueBuilder<?>> defaultValueBuilders = new HashMap<>();

    public DefaultValueBuilders() {
        defaultValueBuilders.put(String.class, new DefaultStringBuilder());
        defaultValueBuilders.put(Integer.class, new DefaultIntegerBuilder());
        defaultValueBuilders.put(int.class, defaultValueBuilders.get(Integer.class));
        defaultValueBuilders.put(Short.class, new DefaultShortBuilder());
        defaultValueBuilders.put(short.class, defaultValueBuilders.get(Short.class));
        defaultValueBuilders.put(Byte.class, new DefaultByteBuilder());
        defaultValueBuilders.put(byte.class, defaultValueBuilders.get(Byte.class));
        defaultValueBuilders.put(Long.class, new DefaultLongBuilder());
        defaultValueBuilders.put(long.class, defaultValueBuilders.get(Long.class));
        defaultValueBuilders.put(Float.class, new DefaultFloatBuilder());
        defaultValueBuilders.put(float.class, defaultValueBuilders.get(Float.class));
        defaultValueBuilders.put(Double.class, new DefaultDoubleBuilder());
        defaultValueBuilders.put(double.class, defaultValueBuilders.get(Double.class));
        defaultValueBuilders.put(Boolean.class, new DefaultBooleanBuilder());
        defaultValueBuilders.put(boolean.class, defaultValueBuilders.get(Boolean.class));
        defaultValueBuilders.put(BigInteger.class, new DefaultBigIntegerBuilder());
        defaultValueBuilders.put(BigDecimal.class, new DefaultBigDecimalBuilder());
        defaultValueBuilders.put(UUID.class, new DefaultUUIDBuilder());
        defaultValueBuilders.put(Date.class, new DefaultDateBuilder());
        defaultValueBuilders.put(Instant.class, new DefaultInstantBuilder());
        defaultValueBuilders.put(LocalDate.class, new DefaultLocalDateBuilder());
        defaultValueBuilders.put(LocalTime.class, new DefaultLocalTimeBuilder());
        defaultValueBuilders.put(LocalDateTime.class, new DefaultLocalDateTimeBuilder());
        defaultValueBuilders.put(OffsetDateTime.class, new DefaultOffsetDateTimeBuilder());
        defaultValueBuilders.put(ZonedDateTime.class, new DefaultZoneDateTimeBuilder());
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<DefaultValueBuilder<T>> query(Class<T> type) {
        return ofNullable((DefaultValueBuilder<T>) defaultValueBuilders.get(type));
    }

    public static class DefaultStringBuilder implements DefaultValueBuilder<String> {

        @Override
        public <T> String create(BeanClass<T> beanType, SubInstance instance) {
            return instance.propertyInfo();
        }
    }

    public static class DefaultLongBuilder implements DefaultValueBuilder<Long> {

        @Override
        public <T> Long create(BeanClass<T> beanType, SubInstance instance) {
            return (long) instance.getSequence();
        }
    }

    public static class DefaultIntegerBuilder implements DefaultValueBuilder<Integer> {

        @Override
        public <T> Integer create(BeanClass<T> beanType, SubInstance instance) {
            return instance.getSequence();
        }
    }

    public static class DefaultShortBuilder implements DefaultValueBuilder<Short> {

        @Override
        public <T> Short create(BeanClass<T> beanType, SubInstance instance) {
            return (short) instance.getSequence();
        }
    }

    public static class DefaultByteBuilder implements DefaultValueBuilder<Byte> {

        @Override
        public <T> Byte create(BeanClass<T> beanType, SubInstance instance) {
            return (byte) instance.getSequence();
        }
    }

    public static class DefaultDoubleBuilder implements DefaultValueBuilder<Double> {

        @Override
        public <T> Double create(BeanClass<T> beanType, SubInstance instance) {
            return (double) instance.getSequence();
        }
    }

    public static class DefaultFloatBuilder implements DefaultValueBuilder<Float> {

        @Override
        public <T> Float create(BeanClass<T> beanType, SubInstance instance) {
            return (float) instance.getSequence();
        }
    }

    public static class DefaultBooleanBuilder implements DefaultValueBuilder<Boolean> {

        @Override
        public <T> Boolean create(BeanClass<T> beanType, SubInstance instance) {
            return (instance.getSequence() % 2) == 1;
        }
    }

    public static class DefaultBigIntegerBuilder implements DefaultValueBuilder<BigInteger> {

        @Override
        public <T> BigInteger create(BeanClass<T> beanType, SubInstance instance) {
            return BigInteger.valueOf(instance.getSequence());
        }
    }

    public static class DefaultBigDecimalBuilder implements DefaultValueBuilder<BigDecimal> {

        @Override
        public <T> BigDecimal create(BeanClass<T> beanType, SubInstance instance) {
            return BigDecimal.valueOf(instance.getSequence());
        }
    }

    public static class DefaultUUIDBuilder implements DefaultValueBuilder<UUID> {

        @Override
        public <T> UUID create(BeanClass<T> beanType, SubInstance instance) {
            return UUID.fromString(String.format("00000000-0000-0000-0000-%012d", instance.getSequence()));
        }
    }

    public static class DefaultDateBuilder implements DefaultValueBuilder<Date> {

        @Override
        public <T> Date create(BeanClass<T> beanType, SubInstance instance) {
            return Date.from(INSTANT_START.plus(instance.getSequence(), ChronoUnit.DAYS));
        }
    }

    public static class DefaultInstantBuilder implements DefaultValueBuilder<Instant> {

        @Override
        public <T> Instant create(BeanClass<T> beanType, SubInstance instance) {
            return INSTANT_START.plusSeconds(instance.getSequence());
        }
    }

    public static class DefaultLocalTimeBuilder implements DefaultValueBuilder<LocalTime> {

        @Override
        public <T> LocalTime create(BeanClass<T> beanType, SubInstance instance) {
            return LOCAL_TIME_START.plusSeconds(instance.getSequence());
        }
    }

    public static class DefaultLocalDateBuilder implements DefaultValueBuilder<LocalDate> {

        @Override
        public <T> LocalDate create(BeanClass<T> beanType, SubInstance instance) {
            return LOCAL_DATE_START.plusDays(instance.getSequence());
        }
    }

    public static class DefaultLocalDateTimeBuilder implements DefaultValueBuilder<LocalDateTime> {

        @Override
        public <T> LocalDateTime create(BeanClass<T> beanType, SubInstance instance) {
            return LOCAL_DATE_TIME_START.plusSeconds(instance.getSequence());
        }
    }

    public static class DefaultOffsetDateTimeBuilder implements DefaultValueBuilder<OffsetDateTime> {

        @Override
        public <T> OffsetDateTime create(BeanClass<T> beanType, SubInstance instance) {
            return INSTANT_START.plusSeconds(instance.getSequence()).atZone(ZoneId.systemDefault()).toOffsetDateTime();
        }
    }

    public static class DefaultZoneDateTimeBuilder implements DefaultValueBuilder<ZonedDateTime> {

        @Override
        public <T> ZonedDateTime create(BeanClass<T> beanType, SubInstance instance) {
            return INSTANT_START.plusSeconds(instance.getSequence()).atZone(ZoneId.systemDefault());
        }
    }

    public static class DefaultTypeBuilder<V> implements DefaultValueBuilder<V> {
        private final BeanClass<V> type;

        public DefaultTypeBuilder(BeanClass<V> type) {
            this.type = type;
        }

        @Override
        public <T> V create(BeanClass<T> beanType, SubInstance instance) {
            return type.createDefault();
        }

        @Override
        public Class<V> getType() {
            return type.getType();
        }
    }
}
