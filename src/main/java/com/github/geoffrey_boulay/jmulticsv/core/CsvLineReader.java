package com.github.geoffrey_boulay.jmulticsv.core;

import lombok.AccessLevel;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;
import java.util.regex.Pattern;

abstract class CsvLineReader<T> implements CsvReader<T> {

    private BufferedReader bufferedReader;

    private String delimiter;

    @Getter(AccessLevel.PACKAGE)
    private boolean closed = false;

    CsvLineReader(String filePath, String defaultDelimiter) throws FileNotFoundException {
        this(new FileReader(filePath), defaultDelimiter);
    }


    CsvLineReader(File file, String defaultDelimiter) throws FileNotFoundException {
        this(new FileReader(file), defaultDelimiter);
    }

    CsvLineReader(final BufferedReader bufferedReader, final String delimiter) {
        this.delimiter = Pattern.quote(delimiter);
        this.bufferedReader = bufferedReader;
    }

    CsvLineReader(final Reader reader, final String delimiter) {
        this.delimiter = Pattern.quote(delimiter);
        this.bufferedReader = new BufferedReader(reader);
    }

    Optional<String[]> readLine() throws IOException {
        if (!closed) {
            String line = bufferedReader.readLine();
            if (line != null) {
                return Optional.of(line.split(delimiter, -1));
            }
        }
        return Optional.empty();
    }


    @Override
    public void close() throws IOException {
        closed = true;
        bufferedReader.close();
    }

}
