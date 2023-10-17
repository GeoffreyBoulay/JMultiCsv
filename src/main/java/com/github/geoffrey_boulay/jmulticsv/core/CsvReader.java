package com.github.geoffrey_boulay.jmulticsv.core;

import java.io.Closeable;
import java.io.IOException;

public interface CsvReader<T> extends Closeable {


    T read() throws IOException;


}
