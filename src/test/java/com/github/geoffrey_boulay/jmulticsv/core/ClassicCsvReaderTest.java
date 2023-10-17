package com.github.geoffrey_boulay.jmulticsv.core;

import com.github.geoffrey_boulay.jmulticsv.annotation.Column;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ClassicCsvReaderTest {

    @Test
    void testClassicCsv() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("7851554;148");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("146546;78457");


        CsvReader<TestClass> csvReader = CsvReaderFactory.instanceOf(new StringReader(stringBuilder.toString()), TestClass.class);

        TestClass line1 = csvReader.read();
        TestClass line2 = csvReader.read();
        TestClass line3 = csvReader.read();

        assertThat(line1).isNotNull();
        assertThat(line1.col1).isEqualTo(7851554);
        assertThat(line1.col2).isEqualTo(148);
        assertThat(line2).isNotNull();
        assertThat(line2.col1).isEqualTo(146546);
        assertThat(line2.col2).isEqualTo(78457);
        assertThat(line3).isNull();

    }

    public static class TestClass {

        @Column(position = 0)
        private int col1;

        @Column(position = 1)
        private int col2;

    }

}
