package com.mikeycaine.examcode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

public class InstantTest {

	@Test
	public void testIsSupported() {
		Instant instant = Instant.now();
		
		assertThat(instant.isSupported(ChronoUnit.MILLENNIA), is(false));
		assertThat(instant.isSupported(ChronoUnit.CENTURIES), is(false));
		assertThat(instant.isSupported(ChronoUnit.DECADES), is(false));
		assertThat(instant.isSupported(ChronoUnit.YEARS), is(false));
		assertThat(instant.isSupported(ChronoUnit.MONTHS), is(false));
		
		assertThat(instant.isSupported(ChronoUnit.DAYS), is(true));
		assertThat(instant.isSupported(ChronoUnit.HOURS), is(true));
		assertThat(instant.isSupported(ChronoUnit.MINUTES), is(true));
		assertThat(instant.isSupported(ChronoUnit.SECONDS), is(true));
		assertThat(instant.isSupported(ChronoUnit.MILLIS), is(true));
		assertThat(instant.isSupported(ChronoUnit.MICROS), is(true));
		assertThat(instant.isSupported(ChronoUnit.NANOS), is(true));
	}
	
	@Test
	public void testIsSupported2() {
		Instant instant = Instant.ofEpochSecond(981_000_000L);
		
		Instant instant2 = Instant.ofEpochSecond(981_000_001L);
		OffsetDateTime gmt2 = instant2.atOffset(ZoneOffset.UTC);
		
		OffsetDateTime gmt = instant.atOffset(ZoneOffset.UTC);
		assertThat(gmt.format(DateTimeFormatter.ISO_ZONED_DATE_TIME), is("2001-02-01T04:00:00Z"));
		
		OffsetDateTime x = instant.atOffset(ZoneOffset.ofHoursMinutes(1, 30));
		assertThat(x.format(DateTimeFormatter.ISO_ZONED_DATE_TIME), is("2001-02-01T05:30:00+01:30"));
		
		ZonedDateTime y = instant.atZone(ZoneId.of("Europe/Paris"));
		assertThat(y.format(DateTimeFormatter.ISO_ZONED_DATE_TIME), is("2001-02-01T05:00:00+01:00[Europe/Paris]"));
		
		Duration d = Duration.between(x, y);
		assertThat(d.get(ChronoUnit.SECONDS), is(0L));
		
		Duration d2 = Duration.between(gmt, x);
		assertThat(d2.get(ChronoUnit.SECONDS), is(0L));
		
		Duration d3 = Duration.between(gmt, gmt2);
		assertThat(d3.get(ChronoUnit.SECONDS), is(1L));
	}
	
	@Test
	public void testInstantToLocalDateTime() {
		Instant instant = Instant.ofEpochSecond(981_000_000L);
		
		LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
		assertThat(ldt.format(DateTimeFormatter.ISO_DATE_TIME), is("2001-02-01T04:00:00"));

		LocalDateTime ldt2 = LocalDateTime.ofInstant(instant, ZoneOffset.ofHours(1));
		assertThat(ldt2.format(DateTimeFormatter.ISO_DATE_TIME), is("2001-02-01T05:00:00"));

		// java.time.temporal.UnsupportedTemporalTypeException: Unsupported field: OffsetSeconds
		//assertThat(ldt2.format(DateTimeFormatter.ISO_ZONED_DATE_TIME), is("2001-02-01T05:00:00+01:00[Europe/Paris]"));

		ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.of("Europe/Paris"));
		assertThat(zdt.format(DateTimeFormatter.ISO_ZONED_DATE_TIME), is("2001-02-01T05:00:00+01:00[Europe/Paris]"));
		
	}
}
