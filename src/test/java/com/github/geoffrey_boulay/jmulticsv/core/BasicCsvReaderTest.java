package com.github.geoffrey_boulay.jmulticsv.core;

import com.github.geoffrey_boulay.jmulticsv.annotation.Column;
import com.github.geoffrey_boulay.jmulticsv.annotation.DateValue;
import com.github.geoffrey_boulay.jmulticsv.annotation.HeaderColumn;
import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BasicCsvReaderTest {

    @Test
    void testMultiCsv1() throws IOException {
        String[] lines = {
                "Main;-48548;20121230",
                "Sub1;7145;;784.57",
                "Sub1bis;-98;785.78;78.57",
                "Sub2;DEF;;",
                "Sub3;geoffrey",
                "OPT;AZERTY",
                "Sub2;GTH;152;-789",
                "Sub3;Phalempin",
                "Main;548;20190306",
                "Sub1;75;.0;784.",
                "Sub1bis;12785;145.15;789"
        };

        CsvReader<Main> csvReader = CsvReaderFactory.instanceOf(new StringReader(Stream.of(lines).collect(Collectors.joining(System.lineSeparator()))), Main.class);

        Main line1 = csvReader.read();
        Main line2 = csvReader.read();
        Main line3 = csvReader.read();

        assertThat(line1).isNotNull();
        assertThat(line1.getCol1()).isEqualTo(-48548);
        assertThat(line1.getCol2()).hasYear(2012).hasMonth(12).hasDayOfMonth(30);

        assertThat(line1.getSub1()).isNotNull();
        assertThat(line1.getSub1().getCol1()).isEqualTo(7145);
        assertThat(line1.getSub1().getCol2()).isNull();
        assertThat(line1.getSub1().getCol3()).isEqualTo(784.57);

        assertThat(line1.getSub1bis()).isNotNull();
        assertThat(line1.getSub1bis().getCol1()).isEqualTo(-98);
        assertThat(line1.getSub1bis().getCol2()).isEqualTo(785.78);
        assertThat(line1.getSub1bis().getCol3()).isEqualTo(78.57);

        assertThat(line1.getSub2s()).hasSize(2);
        assertThat(line1.getSub2s().get(0).getCol1()).isEqualTo("DEF");
        assertThat(line1.getSub2s().get(0).getCol2()).isNull();
        assertThat(line1.getSub2s().get(0).getCol3()).isNull();
        assertThat(line1.getSub2s().get(0).getSub3()).isNotNull();
        assertThat(line1.getSub2s().get(0).getSub3().getCol1()).isEqualTo("geoffrey");
        assertThat(line1.getSub2s().get(0).getOptSub3()).isPresent();
        assertThat(line1.getSub2s().get(0).getOptSub3().get().getCol1()).isEqualTo("AZERTY");

        assertThat(line1.getSub2s().get(1).getCol1()).isEqualTo("GTH");
        assertThat(line1.getSub2s().get(1).getCol2()).isEqualTo(152);
        assertThat(line1.getSub2s().get(1).getCol3()).isEqualTo(-789);
        assertThat(line1.getSub2s().get(1).getSub3()).isNotNull();
        assertThat(line1.getSub2s().get(1).getSub3().getCol1()).isEqualTo("Phalempin");
        assertThat(line1.getSub2s().get(1).getOptSub3()).isEmpty();

        assertThat(line2).isNotNull();
        assertThat(line2.getCol1()).isEqualTo(548);
        assertThat(line2.getCol2()).hasYear(2019).hasMonth(3).hasDayOfMonth(6);
        assertThat(line2.getSub1()).isNotNull();
        assertThat(line2.getSub1().getCol1()).isEqualTo(75);
        assertThat(line2.getSub1().getCol2()).isEqualTo(0);
        assertThat(line2.getSub1().getCol3()).isEqualTo(784);
        assertThat(line2.getSub1bis()).isNotNull();
        assertThat(line2.getSub1bis().getCol1()).isEqualTo(12785);
        assertThat(line2.getSub1bis().getCol2()).isEqualTo(145.15);
        assertThat(line2.getSub1bis().getCol3()).isEqualTo(789);
        assertThat(line2.getSub2s()).isNotNull().isEmpty();

        assertThat(line3).isNull();
    }


    @HeaderColumn("Main")
    @Getter
    @ToString
    public static class Main {

        @Column(position = 1)
        private long col1;

        @Column(position = 2)
        @DateValue(format = "yyyyMMdd")
        private Date col2;

        private Sub1 sub1;

        @HeaderColumn("Sub1bis")
        private Sub1 sub1bis;

        private List<Sub2> sub2s;
    }

    @HeaderColumn("Sub1")
    @Getter
    @ToString
    public static class Sub1 {

        @Column(position = 1)
        private int col1;

        @Column(position = 2)
        private Double col2;

        @Column(position = 3)
        private double col3;
    }


    @HeaderColumn("Sub2")
    @Getter
    @ToString
    public static class Sub2 {

        @Column(position = 1)
        private String col1;

        @Column(position = 2)
        private Integer col2;

        @Column(position = 3)
        private Integer col3;

        private Sub3 sub3;

        @HeaderColumn("OPT")
        private Optional<Sub3> optSub3;
    }

    @HeaderColumn("Sub3")
    @Getter
    @ToString
    public static class Sub3 {

        @Column(position = 1)
        private String col1;

    }

}
