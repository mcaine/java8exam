package com.mikeycaine.examcode;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.LongPredicate;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

import static org.hamcrest.core.Is.*;

public class JavaStreams {

	// Java Streams
	//
	// Describe the Stream interface and pipelines; create a stream by using the
	// Arrays.stream() and IntStream.range() methods; identify the lambda
	// operations that are lazy
	// Develop code that uses parallel streams, including decomposition
	// operation and reduction operation in streams

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
				.limit(2)
				.boxed()
				.collect(Collectors.toList());

		assertThat(result.size(), is(2));
		assertThat(result.get(0), is(4));
		assertThat(result.get(1), is(5));
		assertThat(result, is(Arrays.asList(4, 5)));
	}

	@Test
	public void testArraysStream() {
		Object[] array = { 1, 2, "three", "four" };
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
		IntStream is = IntStream.range(1, 5);
		assertThat(is.boxed().collect(Collectors.toList()).size(), is(4));
	}

	@Test
	public void testOf() {
		Stream<?> myStream = Stream.of("Hello", new Long(31337));
		Stream<Object> myStream2 = Stream.of("Hello", new Long(31337));
		Stream<Number> myStream3 = Stream.of(123456, new Integer(69), new Long(31337L), new Double(Math.PI),
				new AtomicInteger(0), new AtomicLong(747L));

		myStream.forEach((Object o) -> System.out.println(o.toString()));
		myStream2.forEach(System.out::println);
		myStream3.forEach((Number n) -> System.out.println(n.intValue()));

		Number[] myNumbers = { 1, 2, 3, 4L, Math.PI };
		Stream<Number> numberStream = Stream.of(myNumbers);
		Stream<? super Number> numberStream2 = Stream.of(myNumbers);
		Stream<? extends Number> numberStream3 = Stream.of(myNumbers);

		numberStream.forEach(n -> System.out.println(n.intValue()));
		// numberStream2.forEach(n -> System.out.println(n.intValue())); //
		// doesn't compile
		numberStream3.forEach(n -> System.out.println(n.intValue()));
	}

	@Test
	public void testStreamPrimitives() {

		// java.util.stream.DoubleStream
		DoubleStream doubleStream = DoubleStream.of(1.23, 4.56);
		DoubleStream doubleStream2 = DoubleStream.of(1.23F, 4.56F);
		DoubleStream doubleStream3 = DoubleStream.of(1, 2);
		DoubleStream doubleStream4 = DoubleStream.of(1L, 2l);
		DoubleStream doubleStream5 = DoubleStream.of(1L, 2.34);

		// java.util.stream.IntStream
		IntStream intStream = IntStream.of(1, 2, 3);

		// java.util.stream.LongStream
		LongStream longStream = LongStream.of(1L, 2L, 3L);
		LongStream longStream2 = LongStream.of(1, 2, 3);
	}

	@Test(expected = IllegalStateException.class)
	public void testStreamOperatedOn() {
		IntStream nums = IntStream.rangeClosed(1, 10);

		IntStream even = nums.filter(i -> i % 2 == 0);
		IntStream odds = nums.filter(i -> i % 2 != 0);
	}

	@Test
	public void testIntStream() {
		IntStream nums = IntStream.rangeClosed(1, 10);

		IntStream even = nums.filter(i -> i % 2 == 0);
		IntStream evenBig = even.filter(i -> i > 7);

		assertEquals(2, evenBig.count()); // 8 and 10
	}

	@Test
	public void testCollect() {
		
		IntStream is = IntStream.of(23, 24, 25, 26, 27).parallel();
		
		// can't do this
		//is.collect(Collectors.toList());
		
		Supplier<List<String>> supplier = () -> new ArrayList<String>();
		ObjIntConsumer<List<String>> accumulator = (List<String> s, int i) -> s.add(String.valueOf(i));
		BiConsumer<List<String>, List<String>> combiner = (s, s2) -> s.addAll(s2);
		List<String> s =  is.collect(supplier, accumulator, combiner);
		
		s.forEach(System.out::println);

	}

	@Test
	public void testReductions() {

		// sum() and average() are defined only on the primitive stream types.

		// Primitive type Streams
		IntStream intStream = IntStream.empty();
		IntStream intStream2 = IntStream.of(1,2,3);

		OptionalDouble isa = intStream.average(); // java.util.OptionalDouble
		assertFalse(isa.isPresent());
		
		int ist = intStream2.sum();
		
		java.util.function.DoubleConsumer dblConsumer = (double d) -> System.out.println(d);
		DoubleConsumer dblConsumer2 = new DoubleConsumer() {
			@Override
			public void accept(double d2) {
				System.out.println("Consumer 2:" + d2);
			}
		};
		
		isa.ifPresent(dblConsumer);
	}
	
	@Test
	public void testOptional() {
		java.util.Optional<String> os = Optional.of("Hello");
		Optional<String> os2 = Optional.empty();
		
		String name = "Mike";
		Optional<String> os3 = Optional.ofNullable(name);
		
		os.ifPresent(s -> System.out.println(s));
		os2.ifPresent(s -> System.out.println(s));
		
		String greeting = os2.orElse("Goodbye");
		
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
		System.out
				.println("Parallel search found " + numOfPrimes + " primes in " + (end - start) / 1000.0 + " seconds");
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
