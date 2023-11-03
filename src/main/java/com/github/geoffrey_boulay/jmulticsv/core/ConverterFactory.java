package com.github.geoffrey_boulay.jmulticsv.core;


import com.github.geoffrey_boulay.jmulticsv.annotation.BooleanValue;
import com.github.geoffrey_boulay.jmulticsv.annotation.Column;
import com.github.geoffrey_boulay.jmulticsv.annotation.DateValue;
import com.github.geoffrey_boulay.jmulticsv.annotation.DecimalValue;
import com.github.geoffrey_boulay.jmulticsv.exception.CsvParseException;
import com.github.geoffrey_boulay.jmulticsv.util.StringUtils;
import com.github.geoffrey_boulay.jmulticsv.exception.CsvReflectionException;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;


@UtilityClass
class ConverterFactory {

   Function<String, ?> converterOf(Field field) {
        Class<?> type = field.getType();
        Column column = field.getAnnotation(Column.class);
        if (type.equals(String.class)) {
            return Function.identity();
        }
        if (type.equals(int.class)) {
            return Integer::parseInt;
        }
        if (type.equals(Integer.class)) {
            return checkNullValues(Integer::valueOf, column);
        }
        if (type.equals(long.class)) {
            return Long::parseLong;
        }
        if (type.equals(Long.class)) {
            return checkNullValues(Long::valueOf, column);
        }
        if (type.equals(Date.class)) {
            return dateConverter(field);
        }
        if (type.equals(LocalDate.class)) {
            return localDateConverter(field);
        }
        if (type.equals(LocalDateTime.class)) {
            return localDateTimeConverter(field);
        }
        if (type.equals(double.class)) {
            return numberConverter(field, Number::doubleValue, Double::parseDouble);
        }
        if (type.equals(Double.class)) {
            return checkNullValues(numberConverter(field, Number::doubleValue, Double::valueOf), column);
        }
        if (type.equals(float.class)) {
            return numberConverter(field, Number::floatValue, Float::parseFloat);
        }
        if (type.equals(Float.class)) {
            return checkNullValues(numberConverter(field, Number::floatValue, Float::valueOf), column);
        }
        if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            return booleanConverter(field, column);
        }
        try {
            return getStringConstructor(type);
        } catch (NoSuchMethodException e) {
        }
        throw new CsvReflectionException("Unknow type " + type);
    }

    private static Function<String, Boolean> booleanConverter(Field field, Column column) {
        final Optional<BooleanValue> booleanValue = Optional.ofNullable(field.getAnnotation(BooleanValue.class));
        final List<String> trueValues = booleanValue
                .map(BooleanValue::trueValue)
                .map(Arrays::asList)
                .orElseGet(Collections::emptyList);
        final List<String> falseValues = booleanValue
                .map(BooleanValue::falseValue)
                .map(Arrays::asList)
                .orElseGet(Collections::emptyList);;
        if (trueValues.isEmpty() && falseValues.isEmpty()) {
            return checkNullValues(Boolean::valueOf, column);
        }
        return (s) -> {
            if (trueValues.contains(s)) {
                return true;
            }
            if (falseValues.contains(s)) {
                return false;
            }
            if (trueValues.isEmpty()) {
                return false;
            }
            if (falseValues.isEmpty()) {
                return true;
            }
            return null;
        };
    }


    private static <T> Function<String,T> getStringConstructor(Class<T> type) throws NoSuchMethodException {
        final Constructor<T> stringConstructor = type.getConstructor(String.class);
        return (x) -> {
            try {
                return stringConstructor.newInstance(x);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new CsvReflectionException(e);
            }
        };
    }


    private static <T> Function<String, T> checkNullValues(final Function<String, T> delegate, final Column column) {
        final Set<String> nullValues = new TreeSet<>();
        nullValues.addAll(Arrays.asList(column.nullValues()));
        return (x) -> {
            if (nullValues.contains(x)) {
                return null;
            }
            return delegate.apply(x);
        };
    }

    private static Function<String, Date> dateConverter(final Field field) {
        final SimpleDateFormat simpleDateFormat = Optional.of(field)
                .map(f -> f.getAnnotation(DateValue.class))
                .map(DateValue::format)
                .map(SimpleDateFormat::new)
                .orElseGet(SimpleDateFormat::new);
        final Function<String, Date> toDate = (x) -> {
            try {
                return simpleDateFormat.parse(x);
            } catch (ParseException e) {
                throw new CsvParseException("Could not parse date " + x, e);
            }
        };
        return checkNullValues(toDate, field.getAnnotation(Column.class));
    }

    private static Function<String,LocalDate> localDateConverter(final Field field) {
        DateValue dateValue = field.getAnnotation(DateValue.class);
        Function<String,LocalDate> formatter;
        if (dateValue == null) {
            formatter = LocalDate::parse;
        } else {
            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateValue.format());
            formatter = d -> LocalDate.parse(d, dateTimeFormatter);
        }
        return checkNullValues(formatter, field.getAnnotation(Column.class));
    }

    private static Function<String, LocalDateTime> localDateTimeConverter(final Field field) {
        DateValue dateValue = field.getAnnotation(DateValue.class);
        Function<String,LocalDateTime> formatter;
        if (dateValue == null) {
            formatter = LocalDateTime::parse;
        } else {
            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateValue.format());
            formatter = d -> LocalDateTime.parse(d, dateTimeFormatter);
        }
        return checkNullValues(formatter, field.getAnnotation(Column.class));
    }

    private static <X extends Number>  Function<String, X> numberConverter(final Field field, Function<Number, X> numberConverter, Function<String, X> stringConverter) {
        return decimalFormat(field)
                .map(ConverterFactory::parseFunction)
                .map(numberConverter::compose)
                .orElse(stringConverter);
    }

    private static Optional<DecimalFormat> decimalFormat(final Field field) {
        DecimalValue decimalValue = field.getAnnotation(DecimalValue.class);
        if (decimalValue == null) {
            return Optional.empty();
        }
        DecimalFormat decimalFormat = new DecimalFormat(decimalValue.format());
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator(decimalValue.decimalSeparator());
        decimalFormat.setGroupingUsed(decimalValue.decimalGroupingSeparator() != 0);
        if(decimalFormat.isGroupingUsed()) {
            symbols.setGroupingSeparator(decimalValue.decimalGroupingSeparator());
        }
        decimalFormat.setDecimalFormatSymbols(symbols);
        return Optional.of(decimalFormat);
    }

    private static Function<String, Number> parseFunction(DecimalFormat decimalFormat) {
        return (s) -> {
            try {
                return decimalFormat.parse(s);
            } catch (ParseException e) {
                throw new CsvParseException("Could not parse number " + s, e);
            }
        };
    }
}
