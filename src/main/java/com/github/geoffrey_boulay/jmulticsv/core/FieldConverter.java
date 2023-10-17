package com.github.geoffrey_boulay.jmulticsv.core;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Delegate;

import java.lang.reflect.Field;
import java.util.function.Function;

@Value
class FieldConverter<T> implements Function<String,T> {

    private final int position;

    private final Field field;

    @Delegate
    private final Function<String,T> delegate;
}
