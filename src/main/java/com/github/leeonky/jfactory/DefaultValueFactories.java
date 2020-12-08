package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.Optional.ofNullable;

public class DefaultValueFactories {
    private static final LocalDate LOCAL_DATE_START = LocalDate.parse("1996-01-23");
    private static final LocalDateTime LOCAL_DATE_TIME_START = LocalDateTime.parse("1996-01-23T00:00:00");
    private static final LocalTime LOCAL_TIME_START = LocalTime.parse("00:00:00");
    private static final Instant INSTANT_START = Instant.parse("1996-01-23T00:00:00Z");
    private final Map<Class<?>, DefaultValueFactory<?>> defaultValueBuilders = new HashMap<>();

    DefaultValueFactories() {
        register(String.class, new DefaultStringFactory());
        register(Integer.class, new DefaultIntegerFactory());
        register(int.class, defaultValueBuilders.get(Integer.class));
        register(Short.class, new DefaultShortFactory());
        register(short.class, defaultValueBuilders.get(Short.class));
        register(Byte.class, new DefaultByteFactory());
        register(byte.class, defaultValueBuilders.get(Byte.class));
        register(Long.class, new DefaultLongFactory());
        register(long.class, defaultValueBuilders.get(Long.class));
        register(Float.class, new DefaultFloatFactory());
        register(float.class, defaultValueBuilders.get(Float.class));
        register(Double.class, new DefaultDoubleFactory());
        register(double.class, defaultValueBuilders.get(Double.class));
        register(Boolean.class, new DefaultBooleanFactory());
        register(boolean.class, defaultValueBuilders.get(Boolean.class));
        register(BigInteger.class, new DefaultBigIntegerFactory());
        register(BigDecimal.class, new DefaultBigDecimalFactory());
        register(UUID.class, new DefaultUUIDFactory());
        register(Date.class, new DefaultDateFactory());
        register(Instant.class, new DefaultInstantFactory());
        register(LocalDate.class, new DefaultLocalDateFactory());
        register(LocalTime.class, new DefaultLocalTimeFactory());
        register(LocalDateTime.class, new DefaultLocalDateTimeFactory());
        register(OffsetDateTime.class, new DefaultOffsetDateTimeFactory());
        register(ZonedDateTime.class, new DefaultZoneDateTimeFactory());
    }

    public void register(Class<?> type, DefaultValueFactory<?> factory) {
        defaultValueBuilders.put(type, factory);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<DefaultValueFactory<T>> query(Class<T> type) {
        return ofNullable((DefaultValueFactory<T>) defaultValueBuilders.get(type));
    }

    public static class DefaultStringFactory implements DefaultValueFactory<String> {

        @Override
        public <T> String create(BeanClass<T> beanType, SubInstance instance) {
            return instance.propertyInfo();
        }
    }

    public static class DefaultLongFactory implements DefaultValueFactory<Long> {

        @Override
        public <T> Long create(BeanClass<T> beanType, SubInstance instance) {
            return (long) instance.getSequence();
        }
    }

    public static class DefaultIntegerFactory implements DefaultValueFactory<Integer> {

        @Override
        public <T> Integer create(BeanClass<T> beanType, SubInstance instance) {
            return instance.getSequence();
        }
    }

    public static class DefaultShortFactory implements DefaultValueFactory<Short> {

        @Override
        public <T> Short create(BeanClass<T> beanType, SubInstance instance) {
            return (short) instance.getSequence();
        }
    }

    public static class DefaultByteFactory implements DefaultValueFactory<Byte> {

        @Override
        public <T> Byte create(BeanClass<T> beanType, SubInstance instance) {
            return (byte) instance.getSequence();
        }
    }

    public static class DefaultDoubleFactory implements DefaultValueFactory<Double> {

        @Override
        public <T> Double create(BeanClass<T> beanType, SubInstance instance) {
            return (double) instance.getSequence();
        }
    }

    public static class DefaultFloatFactory implements DefaultValueFactory<Float> {

        @Override
        public <T> Float create(BeanClass<T> beanType, SubInstance instance) {
            return (float) instance.getSequence();
        }
    }

    public static class DefaultBooleanFactory implements DefaultValueFactory<Boolean> {

        @Override
        public <T> Boolean create(BeanClass<T> beanType, SubInstance instance) {
            return (instance.getSequence() % 2) == 1;
        }
    }

    public static class DefaultBigIntegerFactory implements DefaultValueFactory<BigInteger> {

        @Override
        public <T> BigInteger create(BeanClass<T> beanType, SubInstance instance) {
            return BigInteger.valueOf(instance.getSequence());
        }
    }

    public static class DefaultBigDecimalFactory implements DefaultValueFactory<BigDecimal> {

        @Override
        public <T> BigDecimal create(BeanClass<T> beanType, SubInstance instance) {
            return BigDecimal.valueOf(instance.getSequence());
        }
    }

    public static class DefaultUUIDFactory implements DefaultValueFactory<UUID> {

        @Override
        public <T> UUID create(BeanClass<T> beanType, SubInstance instance) {
            return UUID.fromString(String.format("00000000-0000-0000-0000-%012d", instance.getSequence()));
        }
    }

    public static class DefaultDateFactory implements DefaultValueFactory<Date> {

        @Override
        public <T> Date create(BeanClass<T> beanType, SubInstance instance) {
            return Date.from(INSTANT_START.plus(instance.getSequence(), ChronoUnit.DAYS));
        }
    }

    public static class DefaultInstantFactory implements DefaultValueFactory<Instant> {

        @Override
        public <T> Instant create(BeanClass<T> beanType, SubInstance instance) {
            return INSTANT_START.plusSeconds(instance.getSequence());
        }
    }

    public static class DefaultLocalTimeFactory implements DefaultValueFactory<LocalTime> {

        @Override
        public <T> LocalTime create(BeanClass<T> beanType, SubInstance instance) {
            return LOCAL_TIME_START.plusSeconds(instance.getSequence());
        }
    }

    public static class DefaultLocalDateFactory implements DefaultValueFactory<LocalDate> {

        @Override
        public <T> LocalDate create(BeanClass<T> beanType, SubInstance instance) {
            return LOCAL_DATE_START.plusDays(instance.getSequence());
        }
    }

    public static class DefaultLocalDateTimeFactory implements DefaultValueFactory<LocalDateTime> {

        @Override
        public <T> LocalDateTime create(BeanClass<T> beanType, SubInstance instance) {
            return LOCAL_DATE_TIME_START.plusSeconds(instance.getSequence());
        }
    }

    public static class DefaultOffsetDateTimeFactory implements DefaultValueFactory<OffsetDateTime> {

        @Override
        public <T> OffsetDateTime create(BeanClass<T> beanType, SubInstance instance) {
            return INSTANT_START.plusSeconds(instance.getSequence()).atZone(ZoneId.systemDefault()).toOffsetDateTime();
        }
    }

    public static class DefaultZoneDateTimeFactory implements DefaultValueFactory<ZonedDateTime> {

        @Override
        public <T> ZonedDateTime create(BeanClass<T> beanType, SubInstance instance) {
            return INSTANT_START.plusSeconds(instance.getSequence()).atZone(ZoneId.systemDefault());
        }
    }

    public static class DefaultTypeFactory<V> implements DefaultValueFactory<V> {
        private final BeanClass<V> type;

        public DefaultTypeFactory(BeanClass<V> type) {
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
