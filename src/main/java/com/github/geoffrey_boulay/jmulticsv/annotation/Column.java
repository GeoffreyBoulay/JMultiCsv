package com.github.geoffrey_boulay.jmulticsv.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    int position();

    String format() default "";


    char decimalSeparator() default '.';

    char decimalGroupingSeparator() default 0;

    String[] nullValues() default {""};

    String[] trueValue() default {};

    String[] falseValue() default {};

}
