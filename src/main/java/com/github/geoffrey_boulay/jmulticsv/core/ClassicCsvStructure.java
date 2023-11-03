package com.github.geoffrey_boulay.jmulticsv.core;

import com.github.geoffrey_boulay.jmulticsv.annotation.Column;
import com.github.geoffrey_boulay.jmulticsv.exception.CsvReflectionException;
import lombok.AccessLevel;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

class ClassicCsvStructure<T> {

    @Getter(AccessLevel.PRIVATE)
    private Supplier<T> factory;

    private List<FieldConverter<?>> fieldConverters = new ArrayList<>();


    ClassicCsvStructure(Class<T> dtoClass) {
        this.factory = () -> {
            try {
                return dtoClass.getConstructor().newInstance();
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new CsvReflectionException(e);
            }
        };
        for (Field field : dtoClass.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    field.setAccessible(true);
                    fieldConverters.add(new FieldConverter<>(column.position(), field));
                }
            }
        }
    }

    public T convert(String[] values) {
        T result = factory.get();
        for (FieldConverter<?> fieldConverter : fieldConverters) {
            String value = values[fieldConverter.getPosition()];
            Object fieldValue = fieldConverter.apply(value);
            try {
                fieldConverter.getField().set(result, fieldValue);
            } catch (IllegalAccessException e) {
                throw new CsvReflectionException(e);
            }
        }
        return result;
    }

}
