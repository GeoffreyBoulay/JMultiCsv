package com.github.geoffrey_boulay.jmulticsv.exception;

/**
 * {@link Exception} thrown when CSV structure is malformed
 */
public class CsvDataStructureException extends CsvException {
    public CsvDataStructureException() {
    }

    public CsvDataStructureException(String message) {
        super(message);
    }

    public CsvDataStructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvDataStructureException(Throwable cause) {
        super(cause);
    }

    public CsvDataStructureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
