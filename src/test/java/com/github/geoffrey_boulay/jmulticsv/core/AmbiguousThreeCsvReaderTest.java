package com.github.geoffrey_boulay.jmulticsv.core;

import com.github.geoffrey_boulay.jmulticsv.annotation.Column;
import com.github.geoffrey_boulay.jmulticsv.annotation.HeaderColumn;
import com.github.geoffrey_boulay.jmulticsv.exception.CsvBadClassStructureException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AmbiguousThreeCsvReaderTest {

    @Test
    void test1_1() {
        Assertions.assertThrows(CsvBadClassStructureException.class, () ->{
            CsvReaderFactory.instanceOf(new StringReader(""), Test1_1.class);
        });
    }

    @Test
    void test1_2() {
        Assertions.assertThrows(CsvBadClassStructureException.class, () ->{
            CsvReaderFactory.instanceOf(new StringReader(""), Test1_2.class);
        });
    }

    @Test
    void test1_3() throws IOException {
        CsvReader<Test1_3> reader = CsvReaderFactory.instanceOf(new StringReader(""), Test1_3.class);

        Test1_3 child = reader.read();

        assertThat(child).isNull();
    }

    @Test
    void test2_1() {
        Assertions.assertThrows(CsvBadClassStructureException.class, () ->{
            CsvReaderFactory.instanceOf(new StringReader(""), Test2_1.class);
        });
    }

    @Test
    void test2_2() throws IOException {
        CsvReader<Test2_2> reader = CsvReaderFactory.instanceOf(new StringReader(""), Test2_2.class);

        Test2_2 child = reader.read();

        assertThat(child).isNull();
    }

    @Test
    void test2_3() throws IOException {
        CsvReader<Test2_3> reader = CsvReaderFactory.instanceOf(new StringReader(""), Test2_3.class);

        Test2_3 child = reader.read();

        assertThat(child).isNull();
    }

    @HeaderColumn("Test1_1")
    public static class Test1_1 {
        @Column(position = 1)
        private String name;

        private Test1_2 child;

    }

    @HeaderColumn("Test1_2")
    public static class Test1_2 {
        @Column(position = 1)
        private String name;

        private Test1_3 child1;

        private Optional<Test1_3> child2;

    }

    @Test
    void test3_1() {
        Assertions.assertThrows(CsvBadClassStructureException.class, () ->{
            CsvReaderFactory.instanceOf(new StringReader(""), Test3_1.class);
        });
    }

    @Test
    void test3_2() throws IOException {
        CsvReader<Test3_2> reader = CsvReaderFactory.instanceOf(new StringReader(""), Test3_2.class);

        Test3_2 child = reader.read();

        assertThat(child).isNull();
    }

    @Test
    void test3_3() throws IOException {
        CsvReader<Test3_3> reader = CsvReaderFactory.instanceOf(new StringReader(""), Test3_3.class);

        Test3_3 child = reader.read();

        assertThat(child).isNull();
    }

    @HeaderColumn("Test1_3")
    public static class Test1_3 {
        @Column(position = 1)
        private String name;

    }

    @HeaderColumn("Test2_1")
    public static class Test2_1 {
        @Column(position = 1)
        private String name;

        private Test2_2 test2_2;

        private Test2_3 test2_3;


    }

    @HeaderColumn("Test2_2")
    public static class Test2_2 {
        @Column(position = 1)
        private String name;

        private Test2_3 test2_3;


    }

    @HeaderColumn("Test2_3")
    public static class Test2_3 {
        @Column(position = 1)
        private String name;

    }

    @HeaderColumn("Test3_1")
    public static class Test3_1 {
        @Column(position = 1)
        private String name;

        private Test3_2 test3_2;

        @HeaderColumn("Test3_2")
        private Test3_3 test3_3;


    }

    @HeaderColumn("Test3_2")
    public static class Test3_2 {
        @Column(position = 1)
        private String name;
    }

    @HeaderColumn("Test3_3")
    public static class Test3_3 {
        @Column(position = 1)
        private String name;
    }
}
