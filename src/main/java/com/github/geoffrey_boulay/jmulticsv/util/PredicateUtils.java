package com.github.geoffrey_boulay.jmulticsv.util;

import lombok.experimental.UtilityClass;

import java.util.function.Predicate;

@UtilityClass
public class PredicateUtils {

    public <T> Predicate<T> not(Predicate<T> predicate) {
        return predicate.negate();
    }

}
