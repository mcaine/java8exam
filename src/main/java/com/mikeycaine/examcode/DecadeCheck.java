package com.mikeycaine.examcode;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.junit.Test;
import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

public class DecadeCheck {
	
	@Test
    public void test() {
        Duration tenYears = ChronoUnit.YEARS.getDuration().multipliedBy(10);
        Duration hundredYears = ChronoUnit.YEARS.getDuration().multipliedBy(100);
        Duration twelveMonths = ChronoUnit.MONTHS.getDuration().multipliedBy(12);
        
        Duration aDecade = ChronoUnit.DECADES.getDuration();
        Duration aCentury = ChronoUnit.CENTURIES.getDuration();
        Duration aYear = ChronoUnit.YEARS.getDuration();
        
        assertThat(tenYears, is(aDecade));
        assertThat(hundredYears, is(aCentury));
        assertThat(twelveMonths, is(aYear));
    }
}