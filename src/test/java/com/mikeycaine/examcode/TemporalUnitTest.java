package com.mikeycaine.examcode;

import org.junit.Test;

import java.time.*;
import java.time.chrono.Chronology;
import java.time.temporal.*;
import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class TemporalUnitTest {

    @Test
    public void test() {
        TemporalUnit ta;

        // public enum ChronoUnit implements TemporalUnit
        // private static enum IsoFields.Unit implements TemporalUnit
        ta = ChronoUnit.SECONDS;
        ta = ChronoUnit.NANOS;
        ta = ChronoUnit.CENTURIES;
        ta = ChronoUnit.FOREVER;
        ta = ChronoUnit.HALF_DAYS;

        ta = IsoFields.QUARTER_YEARS;
        ta = IsoFields.WEEK_BASED_YEARS;
    }

    @Test
    public void test2() {
        Period p;
        //p = Period.parse("P1D");
        p = Period.parse("P100Y2M50D");

        System.out.println("getYears() " + p.getYears());  // getYears() 100
        System.out.println("getMonths() " + p.getMonths()); // getMonths() 2
        System.out.println("getDays() " + p.getDays()); // getDays() 50



        // Unsupported unit: ChronoUnit.NANOS
        // Unsupported unit: ChronoUnit.SECONDS
        // Unsupported unit: ChronoUnit.CENTURIES
        // Unsupported unit: IsoFields.WEEK_BASED_YEARS
        for (TemporalUnit tu : Arrays.asList(ChronoUnit.DAYS, ChronoUnit.MONTHS, ChronoUnit.YEARS)) {
            System.out.println(tu + " " + p.get(tu));
        }
        // Days 50
        // Months 2
        // Years 100

        Chronology chronology = p.getChronology();
        assertTrue(chronology.toString().equals("ISO"));
    }

    @Test
    public void test3() {
        Period period = Period.ofMonths(10000);  // an int
        System.out.println(period); // P10000M

        //normalized() returns a copy of this period with the years and months normalized.
        period = period.normalized();
        System.out.println(period); //833Y4M
    }

    @Test
    public void test4() {
        Period period = Period.ofDays(10000);  // an int
        System.out.println(period); // P10000D
        show(period);

        period = period.normalized();
        System.out.println(period); // P10000D
        show(period);

        period = period.withYears(100);
        System.out.println(period); // P100Y10000D
        show(period);

        period = period.withMonths(100);
        System.out.println(period); // P100Y100M10000D
        show(period);

        period = period.normalized();
        System.out.println(period); // P108Y4M10000D
        show(period);

        period = Period.ofWeeks(10);
        System.out.println(period); // P70D
        show(period);

        period = Period.parse("P7W");
        System.out.println(period); // P49D
        show(period);

    }

    void show(Period period) {
        TemporalAmount ta = period;
        for (TemporalUnit tu : ta.getUnits()) {
            System.out.println(tu + " " + period.get(tu));
        }
        System.out.println();
    }

    @Test
    public void testTemporalAmount() {
        TemporalAmount ta = Duration.parse("P2DT3H4M");
        System.out.println(ta); // PT51H4M
    }

    @Test
    public void testTemporal() {
        Temporal temporal = LocalDateTime.now();
        System.out.println(temporal.getClass());
        Temporal temporal2 = temporal.plus(10, ChronoUnit.HOURS);
        Duration duration = Duration.between(temporal, temporal2);
        System.out.println(duration); // PT10H
    }

    @Test
    public void testTemporal2() {
        Temporal temporal = LocalDate.now();
        System.out.println(temporal.getClass());
        Temporal temporal2 = temporal.plus(5, ChronoUnit.DAYS);
        // Duration duration = Duration.between(temporal, temporal2); //Unsupported unit: Seconds
        Period p = Period.between((LocalDate)temporal, (LocalDate)temporal2); // takes LocalDate not Temporal
        System.out.println(p); // P5D
    }

    @Test
    public void testDuration() {
        Duration duration = Duration.parse("P2D");
        TemporalAmount ta = duration;
        for (TemporalUnit tu : ta.getUnits()) {
            System.out.println(tu + " " + duration.get(tu));
        }
        // Seconds 172800
        // Nanos 0
    }

    @Test
    public void testGetAvailableZoneIds() {
        Set<String> zones = ZoneId.getAvailableZoneIds();
        zones.forEach(System.out::println);
    }

    @Test
    public void testLocalDateTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);

        Clock clock = Clock.systemUTC();
        localDateTime = LocalDateTime.now(clock);
        System.out.println(localDateTime);

        ZoneId zone = ZoneId.of("Asia/Yerevan");
        localDateTime = LocalDateTime.now(zone);
        System.out.println(localDateTime);

    }

    @Test
    public void testZonedDateTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        System.out.println(zonedDateTime);

        Clock clock = Clock.systemUTC();
        zonedDateTime = ZonedDateTime.now(clock);
        System.out.println(zonedDateTime);

        ZoneId zone = ZoneId.of("Asia/Yerevan");
        zonedDateTime = ZonedDateTime.now(zone);
        System.out.println(zonedDateTime);
    }
}
