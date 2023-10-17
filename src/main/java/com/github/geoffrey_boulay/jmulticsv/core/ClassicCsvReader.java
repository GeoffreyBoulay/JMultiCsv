package com.github.geoffrey_boulay.jmulticsv.core;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

class ClassicCsvReader<T> extends CsvLineReader<T> {

    private ClassicCsvStructure<T> structure;

    ClassicCsvReader(String filePath, Class<T> dtoClass, String delimiter) throws FileNotFoundException {
        super(filePath, delimiter);
        this.structure = new ClassicCsvStructure<>(dtoClass);
    }

    ClassicCsvReader(File file, Class<T> dtoClass, String delimiter) throws FileNotFoundException {
        super(file, delimiter);
        this.structure = new ClassicCsvStructure<>(dtoClass);
    }


    ClassicCsvReader(final BufferedReader bufferedReader, final Class<T> dtoClass, final String delimiter) {
        super(bufferedReader, delimiter);
        this.structure = new ClassicCsvStructure<>(dtoClass);
    }

    ClassicCsvReader(final Reader reader, final Class<T> dtoClass, final String delimiter) {
        super(reader, delimiter);
        this.structure = new ClassicCsvStructure<>(dtoClass);
    }


    @Override
    public T read() throws IOException {
        return readLine()
                .map(structure::convert)
                .orElse(null);
    }
}
