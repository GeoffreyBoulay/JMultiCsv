package com.github.geoffrey_boulay.jmulticsv.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

class MultiCsvReader<T> extends CsvLineReader<T> {

    private final MultiCsvTreeClassStructure state;

    MultiCsvReader(String filePath, Class<T> dtoClass, String defaultDelimiter) throws FileNotFoundException {
        super(filePath, defaultDelimiter);
        this.state = new MultiCsvTreeClassStructure(dtoClass);
    }


    MultiCsvReader(File file, Class<T> dtoClass, String defaultDelimiter) throws FileNotFoundException {
        super(file, defaultDelimiter);
        this.state = new MultiCsvTreeClassStructure(dtoClass);
    }


    MultiCsvReader(final BufferedReader bufferedReader, final Class<T> dtoClass, final String delimiter) {
        super(bufferedReader, delimiter);
        this.state = new MultiCsvTreeClassStructure(dtoClass);
    }


    MultiCsvReader(final Reader reader, final Class<T> dtoClass, final String delimiter) {
        super(reader, delimiter);
        this.state = new MultiCsvTreeClassStructure(dtoClass);
    }

    public T read() throws IOException {
        if (!isClosed()) {
            Optional<String[]> line;
            while ((line = readLine()).isPresent()) {
                Object result = state.accept(line.get());
                if (result != null) {
                    return (T) result;
                }
            }
            Object toReturn = state.flush();
            if (toReturn != null) {
                return (T) toReturn;
            }
        }
        return null;
    }


}
