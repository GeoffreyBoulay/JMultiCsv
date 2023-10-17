package com.github.geoffrey_boulay.jmulticsv.core;

import com.github.geoffrey_boulay.jmulticsv.annotation.Column;
import com.github.geoffrey_boulay.jmulticsv.annotation.HeaderColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class FlatCsvReaderTest {


    @Test
    void testSimpleClass() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ABC;DEF;148");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("ABC;7854;78457");


        CsvReader<FlatClass> csvReader = CsvReaderFactory.instanceOf(new StringReader(stringBuilder.toString()), FlatClass.class);

        FlatClass line1 = csvReader.read();
        FlatClass line2 = csvReader.read();
        FlatClass line3 = csvReader.read();


        assertNotNull(line1);
        assertEquals(line1.getCol1(),"DEF");
        assertEquals(line1.getCol2(),148);
        assertNotNull(line2);
        assertEquals(line2.getCol1(),"7854");
        assertEquals(line2.getCol2(), 78457);
        assertNull(line3);
    }




    @HeaderColumn("ABC")
    @NoArgsConstructor
    @Getter
    public static class FlatClass {

        @Column(position = 1)
        private String col1;

        @Column(position = 2)
        private int col2;
    }

}