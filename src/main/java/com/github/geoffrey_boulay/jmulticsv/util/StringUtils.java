package com.github.geoffrey_boulay.jmulticsv.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

    public boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

}
