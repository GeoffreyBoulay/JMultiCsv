package com.github.geoffrey_boulay.jmulticsv.core;

import com.github.geoffrey_boulay.jmulticsv.annotation.Column;
import com.github.geoffrey_boulay.jmulticsv.annotation.HeaderColumn;
import com.github.geoffrey_boulay.jmulticsv.exception.CsvBadClassStructureException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

class AmbiguousRecursiveCsvReaderTest {

    @Test
    void testSimpleRecursive() {
        Assertions.assertThrows(CsvBadClassStructureException.class, () -> {
            CsvReaderFactory.instanceOf(new StringReader(""), SimpleRecursive.class);
        });
    }

    @Test
    void testComplexRecursive1() {
        Assertions.assertThrows(CsvBadClassStructureException.class, () ->{
            CsvReaderFactory.instanceOf(new StringReader(""), ComplexRecursive1.class);
        });
    }

    @Test
    void testComplexRecursive2() {
        Assertions.assertThrows(CsvBadClassStructureException.class, () ->{
            CsvReaderFactory.instanceOf(new StringReader(""), ComplexRecursive2.class);
        });
    }

    @Test
    void testComplexRecursive3() {
        Assertions.assertThrows(CsvBadClassStructureException.class, () ->{
            CsvReaderFactory.instanceOf(new StringReader(""), ComplexRecursive3.class);
        });
    }

    @Test
    void testComplexRecursive4() {
        Assertions.assertThrows(CsvBadClassStructureException.class, () ->{
            CsvReaderFactory.instanceOf(new StringReader(""), ComplexRecursive4.class);
        });
    }

    @Test
    void testComplexRecursive5() {
        Assertions.assertThrows(CsvBadClassStructureException.class, () ->{
            CsvReaderFactory.instanceOf(new StringReader(""), ComplexRecursive5.class);
        });
    }

    @Test
    void testComplexRecursive6() {
        Assertions.assertThrows(CsvBadClassStructureException.class, () ->{
            CsvReaderFactory.instanceOf(new StringReader(""), ComplexRecursive6.class);
        });
    }

    @HeaderColumn("TRT")
    public static class SimpleRecursive {

        @Column(position = 1)
        private int col1;

        private SimpleRecursive recursive;

    }

    @HeaderColumn("1")
    public static class ComplexRecursive1 {
        @Column(position = 1)
        private int col1;

        private ComplexRecursive2 recursive;
    }

    @HeaderColumn("2")
    public static class ComplexRecursive2 {
        @Column(position = 1)
        private int col1;

        private List<ComplexRecursive3> recursive;
    }

    @HeaderColumn("3")
    public static class ComplexRecursive3 {
        @Column(position = 1)
        private int col1;

        private LinkedList<ComplexRecursive4> recursive;
    }

    @HeaderColumn("4")
    public static class ComplexRecursive4 {
        @Column(position = 1)
        private int col1;

        private Optional<ComplexRecursive5> recursive;
    }

    @HeaderColumn("5")
    public static class ComplexRecursive5 {
        @Column(position = 1)
        private int col1;

        private HashSet<ComplexRecursive6> recursive;
    }

    @HeaderColumn("6")
    public static class ComplexRecursive6 {
        @Column(position = 1)
        private int col1;

        private ComplexRecursive2 recursive;
    }

}
