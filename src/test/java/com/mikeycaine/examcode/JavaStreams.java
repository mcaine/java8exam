package com.mikeycaine.examcode;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.*;
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
	public void testGet() {
		Optional<Double> od = Optional.of(Math.PI);
		System.out.println(od.get());

		OptionalDouble od2 = OptionalDouble.of(Math.E);
		System.out.println(od2.getAsDouble());

		OptionalInt oi = OptionalInt.of(31337);
		System.out.println(oi.getAsInt());

		OptionalLong ol = OptionalLong.of(69420);
		System.out.println(ol.getAsLong());
	}

	@Test
	public void testCollect2() {
		List<String> myList = Stream.of("one", "two", "three", "four").collect(Collectors.toList());
		Set<String> mySet = Stream.of("one", "two", "three", "four").collect(Collectors.toSet());

		String result = Stream.of("one", "two", "three", "four").collect(Collectors.joining(","));
		System.out.println(result); // one,two,three,four

		// toCollection takes a Supplier of an empty Collection
		ArrayList<String> myArrayList = Stream.of("one", "two", "three", "four").collect(Collectors.toCollection(() -> new ArrayList<String>()));

		// toMap taking key and value function
		Map<Character, String> myMap = Stream.of("one", "two", "four", "six").collect(Collectors.toMap(s -> s.charAt(0), s -> s));
		System.out.println(myMap); // {s=six, t=two, f=four, o=one}

		// This would have duplicate keys, leading to java.lang.IllegalStateException: Duplicate key t (attempted merging values two and three)
		//Map<Character, String> myMap = Stream.of("one", "two", "four", "six").collect(Collectors.toMap(s -> s.charAt(0), s -> s));

		Map<Character, List<String>> myMap2 = Stream.of("one", "two", "three", "four", "five", "six", "seven").collect(Collectors.groupingBy(s -> s.charAt(0)));
		System.out.println(myMap2); // {s=[six, seven], t=[two, three], f=[four, five], o=[one]}

		Map<Character, String> myMap3 = Stream.of("one", "two", "three", "four", "five", "six", "seven").collect(Collectors.toMap(
				s -> s.charAt(0),
				Function.identity(),
				(String a, String b) -> a + ":" + b
		));
		System.out.println(myMap3); // {s=six:seven, t=two:three, f=four:five, o=one}
	}

	@Test
	public void testCollectPartitioningBy() {
		Map<Boolean, List<Boolean>> result = Stream.of(true).collect(Collectors.partitioningBy(b -> b));
		System.out.println(result); // {false=[], true=[true]}

		Map<Boolean, List<String>> result2 = Stream.of("a", "one", "two", "b", "three", "c").collect(Collectors.partitioningBy(s -> s.length() <= 1));
		System.out.println(result2); // {false=[one, two, three], true=[a, b, c]}
	}

	@Test
	public void testStreamCollections() {
		Set<String> mySet = new HashSet<>();
		mySet.add("one");
		mySet.add("onex");

		List<String> myList = new ArrayList<>();
		myList.add("two");
		myList.add("twox");

		Deque<String> myDeque = new ArrayDeque<>();
		myDeque.add("three");
		myDeque.add("threex");

		// a stream of Collection<String>
		Stream<Collection<String>> colStream = Stream.of(mySet, myList, myDeque);

		// likewise. don't be fooled, this is not the same as mySet.stream()
		Stream<Collection<String>> colStream2 = Stream.of(mySet);

		Stream.of(mySet, myList, myDeque).forEach(System.out::print); //[onex, one][two, twox][three, threex]
		System.out.println();
		Stream.of(mySet, myList, myDeque).flatMap(c -> c.stream()).forEach(System.out::print); // onexonetwotwoxthreethreex
	}

	@Test
	public void streamMaps() {
		Map<String, Integer> things = new HashMap<>();
		things.put("a", 4);
		things.put("b", 8);
		things.put("c", 16);
		things.put("d", 32);

		//things.stream() // can't stream Maps, won't compile

		// can stream the Map's entry set though
		List<String> myList = things.entrySet().stream().map(entry -> entry.getKey() + "=>" + entry.getValue()).collect(Collectors.toList());
		System.out.println(myList); // [a=>4, b=>8, c=>16, d=>32]
	}

	@Test
	public void testStreamPrimitives() {

		// java.util.stream.DoubleStream
		DoubleStream doubleStream = DoubleStream.of(1.23, 4.56);
		DoubleStream doubleStream2 = DoubleStream.of(1.23F, 4.56F);
		DoubleStream doubleStream3 = DoubleStream.of(1, 2);
		DoubleStream doubleStream4 = DoubleStream.of(1L, 2l);
		DoubleStream doubleStream5 = DoubleStream.of(1L, 2.34);

		DoubleSummaryStatistics stats = doubleStream.summaryStatistics();
		double ave = stats.getAverage();

		// java.util.stream.IntStream
		IntStream intStream = IntStream.of(1, 2, 4);
		IntSummaryStatistics intStats = intStream.summaryStatistics();
		double intAve = intStats.getAverage();
		System.out.println("intAve is " + intAve); // intAve is 2.3333333333333335

		IntSummaryStatistics emptyStats = IntStream.empty().summaryStatistics();
		System.out.println("emptyStats ave is " + emptyStats.getAverage());  // emptyStats ave is 0.0

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

		java.util.function.DoubleConsumer dblConsumer = (double d) -> System.out.println(d);
		DoubleConsumer dblConsumer2 = new DoubleConsumer() {
			@Override
			public void accept(double d2) {
				System.out.println("Consumer 2:" + d2);
			}
		};

		DoubleConsumer dblConsumer3 = d -> System.out.println(d);
		DoubleConsumer dblConsumer4 = System.out::println;

		// Primitive type Streams
		IntStream intStream = IntStream.empty();
		OptionalDouble isa = intStream.average(); // java.util.OptionalDouble

		// java.lang.IllegalStateException: stream has already been operated upon or closed
		// int iss = intStream.sum();

		assertFalse(isa.isPresent());
		isa.ifPresent(dblConsumer);
		isa.ifPresent(dbl -> System.out.println("THIS WONT HAPPEN: " + dbl));

		// Can't do this
		//DoubleStream doubleStream = Stream.of(3.141, 2.718, 1.414);

		DoubleStream doubleStream = DoubleStream.of(3.141, 2.718, 1.414);

		LongStream longStream = LongStream.of(1, 2, 3);


		// Can't do this
		// LongStream longStream3 = IntStream.of(1, 2, 3);

		OptionalDouble lsa = longStream.average();


		long lss = LongStream.of(1L, 2, 3L).sum();

		LongPredicate lp = (long l) -> l > 2;
		LongConsumer lc = l -> System.out.println("FOO " + l);
		LongStream longStream2 = LongStream.of(1L, 2, 3L);
		longStream2.filter(lp).forEach(lc); // FOO 3





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
	public void testFind() {
		Stream<String> words = Stream.of("Here", "are", "some", "words");

		// findAny() takes no parameters
		Optional<String> something = words.findAny();

		// this would get
		// java.lang.IllegalStateException: stream has already been operated upon or closed
		// boolean result = words.anyMatch(s -> s.startsWith("w"));

		Stream<String> words2 = Stream.of("Here", "are", "some", "words");
		boolean result = words2.anyMatch(s -> s.startsWith("w"));
		assertTrue(result);

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

		final long MAX_VALUE = 20_000_000;

		long start = System.currentTimeMillis();
		long numOfPrimes = LongStream.rangeClosed(2, MAX_VALUE).filter(isPrime).count();
		long end = System.currentTimeMillis();
		System.out.println("Found " + numOfPrimes + " primes in " + (end - start) / 1000.0 + " seconds");

		start = System.currentTimeMillis();
		numOfPrimes = LongStream.rangeClosed(2, MAX_VALUE).parallel().filter(isPrime).count();
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

	@Test
	public void testMaps() {
		Stream<Integer> myInts = Stream.iterate(1, i -> i + 1).limit(10);

		// Streams have mapTo{Int,Long,Double} and flatMapTo{Int,Long,Double} but NO ..ToObj
		myInts.mapToInt(i -> i * 2).forEach(System.out::println);
	}

	@Test
	public void testMaps2() {
		IntStream is = IntStream.rangeClosed(1, 10);
		IntFunction<String> f = i -> String.valueOf(i);
		is.mapToObj(f).forEach(System.out::println);
	}
}
