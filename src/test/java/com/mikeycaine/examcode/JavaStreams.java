package com.mikeycaine.examcode;

import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.*;
import java.util.stream.*;

import javax.xml.transform.stream.StreamSource;

import javafx.geometry.Insets;
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
	public void testSlowPeek() {
		IntConsumer slowPeek = (int i) -> {
			try {
				Thread.sleep(10000L);
				System.out.println("Peeked and got " + i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};

		IntStream.rangeClosed(1,10).peek(slowPeek).forEach(System.out::println);
	}

	@Test
	public void testCollect3() {

		// Objects
		Stream<Integer> ints = Stream.iterate(new Integer(1), i -> i + 1).limit(10);
		List<Integer> result = ints.collect(Collectors.toList());
		System.out.println(result);

		// Primitives
		IntStream is = IntStream.rangeClosed(1, 10);
		// Can't do this
		//result2 = is.collect(Collectors.toList());
		// have to do
		Supplier<List<Integer>> supplier = () -> new ArrayList<Integer>();
		ObjIntConsumer<List<Integer>> accumulator = (List<Integer> list, int i) -> list.add(new Integer(i));
		BiConsumer<List<Integer>, List<Integer>> combiner = (List<Integer> a, List<Integer> b) -> {
			System.out.println("Called combiner");
			a.addAll(b);
		};
		List<Integer> result2 = is.collect(supplier, accumulator, combiner);
		System.out.println(result2);
	}

	@Test
	public void testParallel2() {
		Stream<Integer> ints = Stream.iterate(new Integer(1), i -> i + 1).limit(10);
		Stream<Integer> p = ints.parallel();

		IntStream is = IntStream.range(1, 10);
		IntStream p2 = is.parallel();
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
		Map<Character, String> myMap = Stream.of("one", "two", "four", "six").collect(toMap(s -> s.charAt(0), s -> s));
		System.out.println(myMap); // {s=six, t=two, f=four, o=one}

		// This would have duplicate keys, leading to java.lang.IllegalStateException: Duplicate key t (attempted merging values two and three)
		//Map<Character, String> myMap = Stream.of("one", "two", "four", "six").collect(Collectors.toMap(s -> s.charAt(0), s -> s));

		Map<Character, List<String>> myMap2 = Stream.of("one", "two", "three", "four", "five", "six", "seven").collect(Collectors.groupingBy(s -> s.charAt(0)));
		System.out.println(myMap2); // {s=[six, seven], t=[two, three], f=[four, five], o=[one]}

		Map<Character, String> myMap3 = Stream.of("one", "two", "three", "four", "five", "six", "seven").collect(toMap(
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
	public void foreachMaps() {
		Map<String, Integer> things = new HashMap<>();
		things.put("a", 4);
		things.put("b", 8);
		things.put("c", 16);
		things.put("d", 32);

		things.forEach((k,v) -> System.out.println("Key: " + k + ", Val: " + v));
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
		long count = stats.getCount();
		double minimum = stats.getMin();
		double sum = stats.getSum();


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
	public void testReduce2() {
		int [] nums = { 1, 2, 3, 4, 5, 69, 420};
		OptionalInt sum = Arrays.stream(nums).reduce((a,b) -> a + b);
		System.out.println("sum is " + sum); // sum is OptionalInt[504]

		int [] nums2 = { 1, 2, 3, 4, 5, 69, 420};
		int sum2 = Arrays.stream(nums).reduce(0, (a,b) -> a + b);
		System.out.println("sum2 is " + sum2); // sum2 is 504
	}
	
	@Test
	public void testTypes() {
		int [] nums = { 1, 2, 3, 4, 5, 69, 420};
		double [] reals = { Math.E, Math.PI};
		String [] names = {"Mike", "Fran", "Fluff", "Sophie"};

		IntStream a = Arrays.stream(nums);
		DoubleStream b = Arrays.stream(reals);
		Stream<String> c = Arrays.stream(names);
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

	@Test
	public void testMaps3() {
		DoubleStream ds = DoubleStream.iterate(1.0, d -> d + 1 ).limit(10);
		DoubleStream ds2 = ds.map(d -> d * d);
		ds2.forEach(System.out::println);
	}

	@Test
	public void testMaps4() {
		IntStream is = IntStream.rangeClosed(1, 10);
		DoubleStream ds2 = is.mapToDouble(i -> i * i);
		ds2.forEach(System.out::println);
	}

	@Test
	public void testMaps5() {
		Stream<String> myStream = Stream.of("red", "yellow", "blue");

		DoubleStream ds2 = myStream.mapToDouble(s -> s.length());
		ds2.forEach(System.out::println); // 3.0 6.0 4.0
	}

	@Test
	public void testMaps6() {
		Stream<String> myStream = Stream.of("red", "yellow", "blue");

		Stream<Integer> ds2 = myStream.map(String::length);
		ds2.forEach(System.out::println); // 3 6 4
	}

	@Test
	public void testMaps7() {
		Stream<String> myStream = Stream.of("red", "yellow", "blue");

		Stream<Double> ds2 = myStream.map(s -> (double)s.length());
		ds2.forEach(System.out::println); // 3.0 6.0 4.0
	}

	@Test
	public void testMaps8() {
		Stream<String> myStream = Stream.of("red", "yellow", "blue");

		Stream<Double> ds2 = myStream.map(s -> (double)s.length());
		ds2.forEach(System.out::println); // 3.0 6.0 4.0
	}

	@Test
	public void testMaps9() {
		Stream<String> myStream = Stream.of("red", "yellow", "blue");

		IntStream ds2 = myStream.mapToInt(s -> s.length());
		Stream<Integer> ds3 = ds2.boxed();
		DoubleStream ds4 = ds3.mapToDouble(i -> 0.5 + i);
		ds4.forEach(System.out::println); // 3.5 6.5 4.5
	}

	@Test
	public void testCollect4() {
		Stream<String> myStream = Stream.of("cat", "dog", "lion", "giraffe", "hedgehog");

		Collector<String, String, String> collector = new Collector<String, String, String>() {

			@Override
			public Supplier<String> supplier() {
				return () -> "";
			}

			@Override
			public BiConsumer<String, String> accumulator() {
				return (a,b) -> System.out.println("Consuming " + a + " and " + b);
			}

			@Override
			public BinaryOperator<String> combiner() {
				return (a,b) -> "Combining " + a + " and " + b;
			}

			@Override
			public Function<String, String> finisher() {
				return a -> "[" + a + "]";
			}

			@Override
			public Set<Characteristics> characteristics() {
				return new HashSet<>();
			}
		};

		String result = myStream.collect(collector);
		System.out.println(result);
	}

	@Test
	public void testMax() {
		Stream<Integer> myInts = Stream.of(1, 2, 3, 69, 420, 31337);

		// can't do this, we need a Comparator
		// myInts.max()
		// myInts.min()

		Optional<Integer> max = Stream.of(1, 2, 3, 69, 420, 31337).max(Comparator.naturalOrder());
		Optional<Integer> max2 = Stream.of(1, 2, 3, 69, 420, 31337).max((a, b) -> a - b);

		System.out.println("max is " + max); // max is Optional[31337]
		System.out.println("max2 is " + max2); // max2 is Optional[31337]
	}

	@Test
	public void testMax2() {
		Stream<String> myStrings = Stream.of("one", "two", "three", "four", "five");
		Optional<String> min = myStrings.max(Comparator.reverseOrder());
		System.out.println("min is " + min); // min is Optional[five]
	}

	@Test
	public void testMaxPrimitives() {
		IntStream myInts = IntStream.of(1, 2, 3, 69, 747, 420);

		// we don't take a Comparator
		OptionalInt max = myInts.max();
	}

	@Test
	public void testOptionalMap() {
		Optional<Integer> optInt = Optional.of(31337);

		Optional<Long> optLong = optInt.map(i -> (long)i);
		Optional<Long> optLong2 = optInt.map(i -> Long.valueOf(i));
		// Optional<Long> optLong3 = optInt.map(i -> i); // not allowed

		OptionalInt maybeInt = OptionalInt.of(747);
		// no map on optionals of primitive types
		//OptionalLong maybeLong = maybeInt.map(i -> (long)i);
	}

	@Test
	public void testGenerate() {
		class Counter {
			int count = 0;
			int getCount() {
				return ++count;
			}
		}

		List<Integer> items = Stream.generate(new Counter()::getCount).limit(10).collect(Collectors.toList());
		System.out.println(items); // [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
	}

	@Test
	public void testIterate() {
		List<Integer> items = Stream.iterate(1, i -> i * 2).limit(10).collect(Collectors.toList());
		System.out.println(items); // [1, 2, 4, 8, 16, 32, 64, 128, 256, 512]
	}

	@Test
	public void testFindFirst() {
		Stream<Integer> myInts = Stream.of(1, 2, 3, 4, 5, 6, 7);
		Optional<Integer> maybeFirst = myInts.findFirst();

		IntStream myIntStream = IntStream.of(1, 2, 3, 4, 5, 6, 7);
		OptionalInt mightBeFirst = myIntStream.findFirst();
	}

	@Test
	public void testFlatMap() {
		IntStream ints = IntStream.of(1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144);
		IntStream twice = ints.flatMap( i -> IntStream.of(i,i));
		twice.forEach(System.out::println);
	}

	@Test
	public void testFlatMap2() {
		DoubleStream doubles = DoubleStream.of(Math.PI, Math.E, 69, 747);
		DoubleStream doubleStream = doubles.flatMap(d -> DoubleStream.of(d,d));
		doubleStream.forEach(System.out::println);
	}

	@Test
	public void testFlatMap3() {
		Stream<Integer> objs = Stream.of(1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144);
		DoubleStream dbls = objs.flatMapToDouble(i -> DoubleStream.of(i, i/3.0));
		dbls.forEach(System.out::println);
	}

	@Test
	public void testCollect5() {
		Stream<String> s = Stream.of("speak", "bark", "meow", "growl");
		Map<Integer, String> map = s.collect(toMap(String::length, k -> k));
		System.out.println(map.size() + " " + map.get(4));
	}


}
