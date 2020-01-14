package com.mikeycaine.examcode;

import org.junit.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;
import java.util.stream.Stream;

public class DateTest {

    @Test
    public void testLocal() {
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        LocalDateTime nowDateTime = LocalDateTime.of(nowDate, nowTime);

        System.out.println("Date is " + nowDate); // Date is 2020-01-07
        System.out.println("Time is " + nowTime); // Time is 13:05:58.388
        System.out.println("DateTime is " + nowDateTime);  // DateTime is 2020-01-07T13:05:58.388
    }

    @Test
    public void testTrump() {
        LocalDate inaugDate = LocalDate.of(2017, 1, 20);
        System.out.println("Inauguration date: " + inaugDate);

        LocalTime inaugTime = LocalTime.of(12,0,0);
        System.out.println("Inauguration time:" + inaugTime);

        LocalDateTime inaugDateTime = LocalDateTime.of(inaugDate, inaugTime);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        System.out.println(formatter.format(inaugDateTime));

        System.out.println("(2) " + DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm").format(inaugDateTime)); // (2) 20-Jan-2017 12:00
        System.out.println("(3) " + DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm").format(inaugDateTime)); // (3) 20-Jan-2017 12:00

        ZonedDateTime zonedInaug = ZonedDateTime.of(inaugDateTime, ZoneId.of("US/Eastern"));

        System.out.println("(4) " + DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a z").format(zonedInaug)); // (4) 20-Jan-2017 12:00 PM EST

        //System.out.println(formatter.format(inaugTime));  // java.time.temporal.UnsupportedTemporalTypeException: Unsupported field: DayOfMonth

    }

    @Test
    public void testFormatters() {
        Instant nowInstant = Instant.now();
        ZonedDateTime zdt = ZonedDateTime.now();

        Stream.of(
                "dd-MM-yyyy HH:mm",
                "d-MMM-yyyy HH:mm",
                "D-MMM-yyyy HH:mm",
                "DD-MM-yyyy HH:mm",
                "dd-MMM-yyyy HH:mm",
                "dd-MM-yyyy hh:mm a",
                "dd-MMMM-yyyy HH:mm",
                "dd-M-yyyy HH:mm"
        ).forEach(pattern -> {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
            String result = zdt.format(dtf);
            System.out.println(pattern + "    " + result);
            System.out.println();
        });

    }

    @Test
    public void testGetZoneIds() {
        ZoneId.getAvailableZoneIds().stream().filter(zid -> zid.toString().contains("Europe")).forEach(zid -> System.out.println(zid));
    }

    @Test(expected = java.time.DateTimeException.class)
    public void testCantFormatDateWithoutZone() {
        LocalDate inaugDate = LocalDate.of(2017, 1, 20);
        LocalTime inaugTime = LocalTime.of(12,0,0);
        LocalDateTime inaugDateTime = LocalDateTime.of(inaugDate, inaugTime);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a z");

        // java.time.DateTimeException: Unable to extract value: class java.time.LocalDateTime
        // since there is no time zone
        System.out.println(formatter.format(inaugDateTime));
    }

    @Test
    public void testZoneIds() {
        ZoneId zoneId = ZoneId.of("Europe/London");
        boolean isDaylightSavings = zoneId.getRules().isDaylightSavings(Instant.now());
        System.out.println("Is Daylight Savings? " + isDaylightSavings); // false (in Jan 2020 ;-)

        ZoneRules zoneRules = zoneId.getRules();
        ZoneOffsetTransition transition = zoneRules.nextTransition(Instant.now());
        System.out.println("Next transition is " + transition); // Next transition is Transition[Gap at 2020-03-29T01:00Z to +01:00]

        System.out.println("Transition duration: " + transition.getDuration()); // Transition duration: PT1H
        LocalDateTime timeBefore = transition.getDateTimeBefore();
        LocalDateTime timeAfter = transition.getDateTimeAfter();

        System.out.println("Before: " + timeBefore); // Before: 2020-03-29T01:00
        System.out.println("After: " + timeAfter); // After: 2020-03-29T02:00
    }

    @Test
    public void testModifiers() {
        LocalDate inaugDate = LocalDate.of(2017, 1, 20);
        LocalTime inaugTime = LocalTime.of(12,0,0);
        LocalDateTime inaugDateTime = LocalDateTime.of(inaugDate, inaugTime);

        LocalDateTime last = inaugDateTime.with(TemporalAdjusters.lastDayOfYear());
        System.out.println("Same time on last day of inaug year: " + last); // Same time on last day of inaug year: 2017-12-31T12:00
    }

    @Test
    public void testParse() {
        ZonedDateTime zd = ZonedDateTime.parse("2020-05-04T08:05:00+00:00[Europe/London]");
        System.out.println(zd.getMonth() + " " + zd.getDayOfMonth()); // MAY 4

        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        ZonedDateTime zd2 = ZonedDateTime.parse("2020-05-04T08:05:00+00:00[Europe/London]", formatter);
        System.out.println(zd2.getMonth() + " " + zd2.getDayOfMonth()); // MAY 4
    }

    @Test(expected = java.time.format.DateTimeParseException.class)
    public void testParseNoOffset() {
        // fails because of missing offset +00:00
        ZonedDateTime zd = ZonedDateTime.parse("2020-05-04T08:05:00[Europe/London]");
    }
}
