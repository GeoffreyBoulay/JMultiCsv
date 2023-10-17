package com.github.geoffrey_boulay.jmulticsv.exception;

/**
 * {@link Exception} thrown when there are a reflection exception in this package
 */
public class CsvReflectionException extends CsvException {

    public CsvReflectionException() {
    }

    public CsvReflectionException(String message) {
        super(message);
    }

    public CsvReflectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvReflectionException(Throwable cause) {
        super(cause);
    }

    public CsvReflectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
