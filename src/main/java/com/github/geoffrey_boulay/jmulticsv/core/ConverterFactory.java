package com.github.geoffrey_boulay.jmulticsv.core;


import com.github.geoffrey_boulay.jmulticsv.annotation.Column;
import com.github.geoffrey_boulay.jmulticsv.exception.CsvParseException;
import com.github.geoffrey_boulay.jmulticsv.util.StringUtils;
import com.github.geoffrey_boulay.jmulticsv.exception.CsvReflectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

class ConverterFactory {

    private ConverterFactory(){}

    static Function<String, ?> converterOf(Class<?> type, Column column) {
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
            return dateConverter(column);
        }
        if (type.equals(LocalDate.class)) {
            return localDateConverter(column);
        }
        if (type.equals(LocalDateTime.class)) {
            return localDateTimeConverter(column);
        }
        if (type.equals(double.class)) {
            return numberConverter(column, Number::doubleValue, Double::parseDouble);
        }
        if (type.equals(Double.class)) {
            return checkNullValues(numberConverter(column, Number::doubleValue, Double::valueOf), column);
        }
        if (type.equals(float.class)) {
            return numberConverter(column, Number::floatValue, Float::parseFloat);
        }
        if (type.equals(Float.class)) {
            return checkNullValues(numberConverter(column, Number::floatValue, Float::valueOf), column);
        }
        if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            return booleanConverter(column);
        }
        try {
            return getStringConstructor(type);
        } catch (NoSuchMethodException e) {
        }
        throw new CsvReflectionException("Unknow type " + type);
    }

    private static Function<String, Boolean> booleanConverter(Column column) {
        final List<String> trueValues = Arrays.asList(column.trueValue());
        final List<String> falseValues = Arrays.asList(column.falseValue());
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

    private static Function<String, Date> dateConverter(final Column column) {
        String format = column.format();
        final SimpleDateFormat simpleDateFormat;
        if (StringUtils.isEmpty(format)) {
            simpleDateFormat = new SimpleDateFormat();
        } else {
            simpleDateFormat = new SimpleDateFormat(format);
        }
        final Function<String, Date> toDate = (x) -> {
            try {
                return simpleDateFormat.parse(x);
            } catch (ParseException e) {
                throw new CsvParseException("Could not parse date " + x, e);
            }
        };
        return checkNullValues(toDate, column);
    }

    private static Function<String,LocalDate> localDateConverter(final Column column) {
        String format = column.format();
        Function<String,LocalDate> formatter;
        if (StringUtils.isEmpty(format)) {
            formatter = LocalDate::parse;
        } else {
            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
            formatter = d -> LocalDate.parse(d, dateTimeFormatter);
        }
        return checkNullValues(formatter, column);
    }

    private static Function<String, LocalDateTime> localDateTimeConverter(final Column column) {
        String format = column.format();
        Function<String,LocalDateTime> formatter;
        if (StringUtils.isEmpty(format)) {
            formatter = LocalDateTime::parse;
        } else {
            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
            formatter = d -> LocalDateTime.parse(d, dateTimeFormatter);
        }
        return checkNullValues(formatter, column);
    }

    private static <X extends Number>  Function<String, X> numberConverter(final Column column, Function<Number, X> numberConverter, Function<String, X> stringConverter) {
        return decimalFormat(column)
                .map(ConverterFactory::parseFunction)
                .map(numberConverter::compose)
                .orElse(stringConverter);
    }

    private static Optional<DecimalFormat> decimalFormat(final Column column) {
        if (column.format().isEmpty()) {
            return Optional.empty();
        }
        DecimalFormat decimalFormat = new DecimalFormat(column.format());
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator(column.decimalSeparator());
        decimalFormat.setGroupingUsed(column.decimalGroupingSeparator() != 0);
        if(decimalFormat.isGroupingUsed()) {
            symbols.setGroupingSeparator(column.decimalGroupingSeparator());
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
