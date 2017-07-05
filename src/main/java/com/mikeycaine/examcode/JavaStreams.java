package com.mikeycaine.examcode;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.Test;

import static org.hamcrest.core.Is.*;

public class JavaStreams {
	
//  Java Streams
//
//  Describe the Stream interface and pipelines; create a stream by using the Arrays.stream() and  IntStream.range() methods; identify the lambda operations that are lazy
//  Develop code that uses parallel streams, including decomposition operation and reduction operation in streams

	@Test
	public void test() {
		// Prints:
		// 4
		// 5
		
		List<Integer> result = 
		Stream.of("ace ", "jack ", "queen ", "king ", "joker ")
        .mapToInt(card -> card.length())
        .filter(len -> len > 3)
        .peek(System.out::println)
        .limit(2).boxed().collect(Collectors.toList());
		
		assertThat(result.size(), is(2));
		assertThat(result.get(0), is(4));
		assertThat(result.get(1), is(5));
		assertThat(result, is (Arrays.asList(4, 5)));
	}
	
	@Test
	public void testArraysStream() {
		Object[] array = { 1, 2, "three", "four"};
		Stream<Object> x = Arrays.stream(array);
		
		String[] stringArray = { "one", "two" };
		Stream<String> stringStream = Arrays.stream(stringArray);
		Stream<String> anotherStringStream = Stream.of("three", "four");
		Stream<?> anotherStream = Stream.of("three", "four", 5, 6);
		Stream<Object> yetAnotherStream = Stream.of("three", "four", 5, 6);
		Stream<Number> numberStream = Stream.of(1, 2, 0.0, 5L, 3.141F);
	}
	
	@Test
	public void testIntStreamRange() {
		IntStream is = IntStream.range(1,5);
		assertThat(is.boxed().collect(Collectors.toList()).size(), is(4));
	}
	
	@Test
	public void testOf() {
		Stream<?> myStream = Stream.of("Hello", new Long(31337));
		Stream<Object> myStream2 = Stream.of("Hello", new Long(31337));
		Stream<Number> myStream3 = Stream.of(123456, new Integer(69), new Long(31337L), new Double(Math.PI), new AtomicInteger(0), new AtomicLong(747L));
		
		myStream.forEach((Object o) -> System.out.println(o.toString()));
		myStream2.forEach(System.out::println);
		myStream3.forEach((Number n) -> System.out.println(n.intValue()));
		
		Number[] myNumbers = {1, 2, 3, 4L, Math.PI};
		Stream<? super Number> numberStream = Stream.of(myNumbers);
		Stream<? extends Number> numberStream2 = Stream.of(myNumbers);
		
		//numberStream.forEach(n -> System.out.println(n.intValue()));  // doesn't compile
		numberStream2.forEach(n -> System.out.println(n.intValue()));
	}
	
	@Test
	public void testParallel() {
		LongPredicate isPrime = new LongPredicate() {
			@Override
			public boolean test(long val) {
				for (long i = 2; i <= Math.sqrt(val); i++) {
		            if (val % i == 0) {
		                return false;
		            }
		        }
		        return true;
			}
		};
		
		assertTrue(isPrime.test(2));
		assertTrue(isPrime.test(3));
		assertFalse(isPrime.test(4));
		assertTrue(isPrime.test(5));
		assertFalse(isPrime.test(6));
		
		long maxValue = 20_000_000;
		
		long start = System.currentTimeMillis();
		long numOfPrimes = LongStream.rangeClosed(2, maxValue).filter(isPrime).count();
		long end = System.currentTimeMillis();
		System.out.println("Found " + numOfPrimes + " primes in " + (end - start) / 1000.0 + " seconds");
		
		start = System.currentTimeMillis();
		numOfPrimes = LongStream.rangeClosed(2, maxValue).parallel().filter(isPrime).count();
		end = System.currentTimeMillis();
		System.out.println("Parallel search found " + numOfPrimes + " primes in " + (end - start) / 1000.0 + " seconds");
	}
	
	@Test
	public void testReduce() {
		String s = "the quick brown fox jumps over the lazy dog ";
		for (int i = 0; i < 14; ++i) {
			s = s + s;
		}
		
        String words[] = s.split(" ");
        
        System.out.println("There are " + words.length + " words");
        Stream<String> str = Arrays.stream(words);
        Stream<String> str2 = Arrays.stream(words);
        
        long start = System.currentTimeMillis();
        Optional<String> originalString = str.parallel().reduce((a, b) -> a + " " + b);
        long end = System.currentTimeMillis();
        System.out.println("Parallel took " + (end - start) / 1000.0 + " seconds");
        
        start = System.currentTimeMillis();
        originalString = str2.reduce((a, b) -> a + " " + b);
        end = System.currentTimeMillis();
        System.out.println("Non-parallel took " + (end - start) / 1000.0 + " seconds");
	}
}
