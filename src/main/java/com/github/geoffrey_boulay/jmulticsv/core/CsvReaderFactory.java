package com.github.geoffrey_boulay.jmulticsv.core;

import com.github.geoffrey_boulay.jmulticsv.annotation.HeaderColumn;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;

@UtilityClass
public class CsvReaderFactory {


    public <T> CsvReader<T> instanceOf(File file, Class<T> beanClass) throws FileNotFoundException {
        return instanceOf(file, beanClass, CsvConstant.DEFAULT_DELIMITER);
    }

    public <T> CsvReader<T> instanceOf(File file, Class<T> beanClass, String delimiter) throws FileNotFoundException {
        if (isMultiCsv(beanClass)) {
            return new MultiCsvReader<>(file, beanClass, delimiter);
        }
        return new ClassicCsvReader<>(file, beanClass, delimiter);
    }

    public <T> CsvReader<T> instanceOf(String path, Class<T> beanClass) throws FileNotFoundException {
        return instanceOf(path, beanClass, CsvConstant.DEFAULT_DELIMITER);
    }
    public <T> CsvReader<T> instanceOf(String path, Class<T> beanClass, String delimiter) throws FileNotFoundException {
        if (isMultiCsv(beanClass)) {
            return new MultiCsvReader<>(path, beanClass, delimiter);
        }
        return new ClassicCsvReader<>(path, beanClass, delimiter);
    }

    public <T> CsvReader<T> instanceOf(Reader reader, Class<T> beanClass) {
        return instanceOf(reader, beanClass, CsvConstant.DEFAULT_DELIMITER);
    }

    public <T> CsvReader<T> instanceOf(Reader reader, Class<T> beanClass, String delimiter)  {
        if (isMultiCsv(beanClass)) {
            return new MultiCsvReader<>(reader, beanClass, delimiter);
        }
        return new ClassicCsvReader<>(reader, beanClass, delimiter);
    }


    private boolean isMultiCsv(Class<?> beanClass) {
        return beanClass.getAnnotation(HeaderColumn.class) != null;
    }

}
