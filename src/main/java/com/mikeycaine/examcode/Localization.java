package com.mikeycaine.examcode;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.JulianFields;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.*;

public class Localization {
	
//  Localization
//
//  Describe the advantages of localizing an application and developing code that defines, reads, and sets the locale with a Locale object
//  Build a resource bundle for a locale and call a resource bundle from an application
//  Create and manage date- and time-based events by using LocalDate, LocalTime, LocalDateTime, Instant, Period, and Duration, including a combination of date and time in a single object
//  Format dates, numbers, and currency values for localization with the NumberFormat and DateFormat classes, including number and date format patterns
//  Work with dates and times across time zones and manage changes resulting from daylight savings
	
	public void printLocaleDetails(Locale locale) {
		System.out.println();
		System.out.println("Locale: " + locale);
		System.out.println("Language: " + locale.getLanguage() + " " + locale.getDisplayLanguage());
		System.out.println("Country: " + locale.getCountry() + " (" + locale.getDisplayCountry() + ")");
		System.out.println("Script: " + locale.getDisplayScript());
		System.out.println("Variant: " + locale.getDisplayVariant());
		System.out.println();
	}
	
	@Test
	public void testAvailableLocales() {
		Locale[] locales = Locale.getAvailableLocales();
		Arrays.stream(locales).forEach((Locale l) -> System.out.printf("Locale %s is %s with language tag %s%n", l, l.getDisplayName(), l.toLanguageTag()));
	}
	
	@Test
	public void testAvailableLocalesInEnglish() {
		Arrays.stream(Locale.getAvailableLocales()).filter(l -> l.getLanguage().equals("en"))
			.forEach((Locale l) -> System.out.printf("Locale %s is %s with language tag %s%n", l, l.getDisplayName(), l.toLanguageTag()));
	}
	
	@Test
	public void testGetDefaultLocale() {
		printLocaleDetails(Locale.getDefault());
		
		Locale.setDefault(Locale.ENGLISH);
		printLocaleDetails(Locale.getDefault());
		
		printLocaleDetails(Locale.CHINESE);
	}
	
	@Test
	public void testCreateLocale() {
		Locale loc1 = new Locale("it", "", "");
		Locale loc2 = Locale.forLanguageTag("it");
		Locale loc3 = new Locale.Builder().setLanguageTag("it").setRegion("IT").build();
		Locale loc4 = Locale.ITALIAN;
		Locale loc5 = Locale.ITALY;
		
		printLocaleDetails(loc3);
		printLocaleDetails(loc4);
		printLocaleDetails(loc5);
	}
		
	@Test
	public void testResourceBundle() {
		Locale currentLocale = Locale.getDefault();
		ResourceBundle res = ResourceBundle.getBundle("ResourceBundle", currentLocale);
		System.out.println(res.getString("Greeting"));
		
		Locale.setDefault(Locale.forLanguageTag("ar"));
		System.out.println(ResourceBundle.getBundle("ResourceBundle", Locale.getDefault()).getString("Greeting"));
		
		Locale.setDefault(Locale.forLanguageTag("it"));
		System.out.println(ResourceBundle.getBundle("ResourceBundle", Locale.getDefault()).getString("Greeting"));
	}
	
	@Test
	public void testListResourceBundle() {
		Locale currentLocale = Locale.getDefault();
		ResourceBundle res = ResourceBundle.getBundle("ResBundle", currentLocale);
		System.out.println(res.getString("MovieName"));
		
		Locale.setDefault(new Locale("it", "IT", ""));
		System.out.println(ResourceBundle.getBundle("ResBundle", Locale.getDefault()).getString("MovieName"));
	}
	
	@Test
	@Ignore
	public void testCantConstructLocaleFromLocale() {
		Locale currentLocale = Locale.getDefault();
		
		// The constructor Locale(Locale) is undefined
		//Locale same = new Locale(currentLocale);
	}
	
	@Test
	public void testLocaleToString() {
		Locale locale = new Locale("navi", "pandora");
		assertThat(locale.toString(), is("navi_PANDORA"));
	}
	
	// Create and manage date- and time-based events by using LocalDate, LocalTime, LocalDateTime, Instant, Period, and Duration, including a combination of date and time in a single object
	@Test
	public void testLocalDate() {
		LocalDate localDate = LocalDate.now();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
		System.out.println(localDate.format(dateFormatter));
		
		LocalDate newYear2016 = LocalDate.of(2016, 1, 1);
		LocalDate anotherNewYear2016 = LocalDate.of(2016, Month.JANUARY, 1);
		LocalDate yetAnotherNewYear2016 = LocalDate.ofYearDay(2016, 1);
		LocalDate moreNewYear2016 = LocalDate.parse("2016-01-01");
		
		System.out.println("New Year 2016: " + newYear2016.format(dateFormatter));
		System.out.println("Another New Year 2016: " + anotherNewYear2016.format(dateFormatter));
		System.out.println("Yet Another New Year 2016: " + yetAnotherNewYear2016.format(dateFormatter));
		System.out.println("More New Year 2016: " + moreNewYear2016.format(dateFormatter));
		
		LocalDate dateOfBirth = LocalDate.of(1988, Month.NOVEMBER, 4);
		MonthDay monthDay =  MonthDay.of(dateOfBirth.getMonth(), dateOfBirth.getDayOfMonth());
		boolean ifTodayBirthday = monthDay.equals(MonthDay.from(LocalDate.now()));
		System.out.println(ifTodayBirthday ? "Happy birthday!" : "Yet another day!");
	}
	
	@Test
	public void testLocalDateWithClock() {
		Clock clock = Clock.systemUTC();
		LocalDate localDate = LocalDate.now(clock);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
		System.out.println(localDate.format(dateFormatter));
	}
	
	@Test
	public void testLocalDateTime() {
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("hh:mm:ss dd/MMM/yyyy");
		System.out.println(localDateTime.format(dateFormatter));
		
		LocalTime localTime = localDateTime.toLocalTime();
		LocalDate localDate = localDateTime.toLocalDate();
	}
	
	@Test
	public void testLocalDateTimeWithClock() {
		//Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
		//availableZoneIds.stream().forEach(System.out::println);
		
		Clock clock = Clock.system(ZoneId.of("America/Los_Angeles"));
		LocalDateTime localDateTime = LocalDateTime.now(clock);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("hh:mm:ss dd/MMM/yyyy");
		System.out.println(localDateTime.format(dateFormatter));
	}
	
	// java.time.temporal.UnsupportedTemporalTypeException: Unsupported field: ClockHourOfAmPm
	@Test(expected = UnsupportedTemporalTypeException.class)
	public void testCantFormatTimePartOfDate() {
		LocalDate localDate = LocalDate.now();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("hh:mm:ss dd/MMM/yyyy");
		System.out.println(localDate.format(dateFormatter));
	}
	

	@Test
	public void testLocalTime() {
		LocalTime localTime = LocalTime.now();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		System.out.println(localTime.format(dateFormatter));
		
		LocalTime halfPastEight = LocalTime.of(18, 30);
		System.out.println("Half past eight: " + halfPastEight.format(dateFormatter));
		
		LocalTime homeTime = LocalTime.parse("17:30:01");
		System.out.println("Home time: " + homeTime.format(dateFormatter));
	}
	
	// java.time.temporal.UnsupportedTemporalTypeException: Unsupported field: DayOfMonth
	@Test(expected = UnsupportedTemporalTypeException.class)
	public void testCantFormatDatePartOfLocalTime() {
		LocalTime localTime = LocalTime.now();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("hh:mm:ss dd/MMM/yyyy");
		System.out.println(localTime.format(dateFormatter));
	}
	
	@Test
	public void testInstant() {
		Instant instant = Instant.now();
		Instant instant2 = Instant.MAX;
		Instant instant3 = Instant.MIN;
		Instant instant4 = Instant.EPOCH;
		
//		TemporalField temporalField = ChronoField.DAY_OF_MONTH;
//		TemporalField temporalField2 = IsoFields.DAY_OF_QUARTER;
//		TemporalField temporalField3 = JulianFields.MODIFIED_JULIAN_DAY;
//		
//		TemporalAccessor temporalAccessor = DayOfWeek.FRIDAY;
//		Instant instant5 = Instant.from(temporalAccessor);
	}
	
	@Test
	public void testPeriod() {
		LocalDate start = LocalDate.of(2017, 2, 14);
		LocalDate end = LocalDate.of(2017, 4, 1);
		
		Period difference = Period.between(start, end);
		System.out.println("Difference: " + difference);
		System.out.println("Difference: " + difference.getMonths() + " months, " + difference.getDays() + " days");
	}
	
	@Test
	public void testDuration() {
		LocalDateTime now = LocalDateTime.now();
		for(int i = 0; i < 1000000; ++i);
		LocalDateTime then = LocalDateTime.now();
		
		Duration durDiff = Duration.between(now, then);
		System.out.println("Duration Difference: " + durDiff);
		
		Instant epoch = Instant.EPOCH;
		Instant instantNow = Instant.now();
		
		Duration durDiff2 = Duration.between(epoch, instantNow);
		System.out.println("Duration Difference 2: " + durDiff2);
	}
	
	@Test
	public void testTemporalUnit() {
		Arrays.asList(ChronoUnit.values()).stream().forEach(System.out::println);
		
		TemporalUnit t = ChronoUnit.MINUTES;
		TemporalUnit t2 = ChronoUnit.FOREVER;
		
		Duration fiveMinutes = Duration.of(5, ChronoUnit.MINUTES);
		System.out.println("5 minutes is " + fiveMinutes.getSeconds() + " seconds");
	}
	
	@Test
	public void testZonedDateTime() {
		LocalDate currentDate = LocalDate.now();
		LocalTime currentTime = LocalTime.now();
		
		ZoneId.SHORT_IDS.entrySet().forEach(e -> System.out.printf("%s = %s%n", e.getKey(), e.getValue()));
	}
}


