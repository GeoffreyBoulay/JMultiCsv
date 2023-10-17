package com.github.geoffrey_boulay.jmulticsv.exception;

/**
 * {@link Exception} thrown when DTO class as ambiguous mapping
 */
public class CsvBadClassStructureException extends CsvException {

    public CsvBadClassStructureException() {}

    public CsvBadClassStructureException(String message) {
        super(message);
    }

    public CsvBadClassStructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvBadClassStructureException(Throwable cause) {
        super(cause);
    }

    public CsvBadClassStructureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
