package com.github.geoffrey_boulay.jmulticsv.core;

import lombok.Getter;
import lombok.experimental.Delegate;

import java.lang.reflect.Field;
import java.util.function.Function;

class FieldConverter<T> implements Function<String,T> {

    @Getter
    private final int position;

    @Getter
    private final Field field;

    @Delegate
    private final Function<String,T> converter;


    public FieldConverter(int position, Field field) {
        this.position = position;
        this.field = field;
        this.converter = (Function<String,T>) ConverterFactory.converterOf(field);
    }
}
