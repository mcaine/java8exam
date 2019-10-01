package com.mikeycaine.examcode;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;
import static org.hamcrest.core.Is.*;

public class JavaCollections {
	
//  Java Collections
//
//  Develop code that uses diamond with generic declarations
//  Develop code that iterates a collection, filters a collection, and sorts a collection by using lambda expressions
//  Search for data by using methods, such as findFirst(), findAny(), anyMatch(), allMatch(), and noneMatch()
//  Perform calculations on Java Streams by using count, max, min, average, and sum methods and save results to a collection by using the collect method and Collector class, including the averagingDouble, groupingBy, joining, partitioningBy methods
//  Develop code that uses Java SE 8 collection improvements, including the Collection.removeIf(), List.replaceAll(), Map.computeIfAbsent(), and Map.computeIfPresent() methods
//  Develop  code that uses the merge(), flatMap(), and map() methods on Java Streams

//  Develop code that uses diamond with generic declarations
	@Test
	public void testDiamond() {
		List<String> myList = new ArrayList<>();
		assertTrue(myList.size() == 0);
	}

//  Develop code that iterates a collection, filters a collection, and sorts a collection by using lambda expressions
	@Test
	public void testIterator() {
		List<String> myList = Arrays.asList("one", "two", "three", "four");
		List<String> result = new ArrayList<>();
		for (String s : myList) {
			result.add(s);
		}
		assertTrue(result.size() == 4);
		
		List<String> result2 = new ArrayList<>();
		Iterator<String> it = myList.iterator();
		while(it.hasNext()) {
			result2.add(it.next());
		}
		assertTrue(result2.size() == 4);
	}
	
	@Test
	public void testFilter() {
		Predicate<String> isUpperCase = s -> s.toUpperCase().equals(s);
		
		List<String> myList = Arrays.asList("one", "Two", "three", "FOUR");
		List<String> result = myList.stream().filter(isUpperCase).collect(Collectors.toList());
		
		assertTrue(result.size() == 1);
		assertTrue(result.contains("FOUR"));
	}
	
	@Test
	public void testSort() {
		List<String> myList = Arrays.asList("one", "Two", "three", "FOUR", "fiVe", "SIX", "seven");
		
		Comparator<String> comp = (s1, s2) -> s1.toUpperCase().compareTo(s2.toUpperCase());
		myList.sort(comp);
		
		assertTrue(myList.get(0).equals("fiVe"));
	}
	
//  Search for data by using methods, such as findFirst(), findAny(), anyMatch(), allMatch(), and noneMatch()
	@Test
	public void testFindFirst() {
		List<String> myList = Arrays.asList("one", "Two", "three", "FOUR", "fiVe", "SIX", "seven");
		Optional<String> result = myList.stream().findFirst();
		assertTrue(result.get().equals("one"));
	}
	
	@Test
	public void testFindAny() {
		List<String> myList = Arrays.asList("one", "Two", "three", "FOUR", "fiVe", "SIX", "seven");
		Optional<String> result = myList.stream().findAny();
		assertTrue(result.get().length() > 0);
		
		List<String> emptyList = Arrays.asList();
		Optional<String> result2 = emptyList.stream().findAny();
		assertFalse(result2.isPresent());
	}
	
	@Test
	public void testAnyMatch() {
		List<String> myList = Arrays.asList("one", "Two", "three", "FOUR", "fiVe", "SIX", "seven");
		boolean result = myList.stream().anyMatch(s -> s.toUpperCase().equals("FIVE"));
		assertTrue(result);
	}
	
	@Test
	public void testAllMatch() {
		List<String> myList = Arrays.asList("one", "two", "three");
		List<String> myList2 = Arrays.asList("one", "TWO", "three");
		
		Predicate<String> isLowerCase = s -> s.toLowerCase().equals(s);
		
		assertTrue(myList.stream().allMatch(isLowerCase));
		assertFalse(myList2.stream().allMatch(isLowerCase));	
	}
	
	@Test
	public void testNoneMatch() {
		List<String> myList = Arrays.asList("one", "two", "three");
		List<String> myList2 = Arrays.asList("one", "TWO", "three");
		List<String> myList3 = Arrays.asList("ONE", "TWO", "THREE");
		
		Predicate<String> isLowerCase = s -> s.toLowerCase().equals(s);
		Predicate<String> isUpperCase = s -> s.toUpperCase().equals(s);
		
		assertTrue(myList.stream().noneMatch(isUpperCase));
		assertFalse(myList.stream().noneMatch(isLowerCase));
		
		assertFalse(myList2.stream().noneMatch(isLowerCase));		
		assertFalse(myList2.stream().noneMatch(isUpperCase));
		
		assertTrue(myList3.stream().noneMatch(isLowerCase));
		assertFalse(myList3.stream().noneMatch(isUpperCase));
	}
	
	// Perform calculations on Java Streams by using count, max, min, average, and sum methods and save results to a collection by using the collect method and Collector class, including the averagingDouble, groupingBy, joining, partitioningBy methods
	@Test
	public void testCount() {
		long count = Stream.of(1, 2, 3, 4, 5).map(i -> i * i).count();
		assertThat(count, is(5L));
	}
	
	@Test
	public void testMax() {
		IntStream myStream = IntStream.range(1, 100);
		OptionalInt max = myStream.map(i -> i * i).max();
		assertThat(max.getAsInt(), is(99 * 99));
		
		Stream<Integer> myStream2 = Stream.of(1,2,3,44,55,6);
		Optional<Integer> max3 = myStream2.max(Integer::compare);
		assertThat(max3.get(), is(55));
		
		Stream<Integer> myStream3 = Stream.of(1,2,3,44,55,6);
		Optional<Integer> max4 = myStream3.map(i -> i * i).max(Integer::compare);
		assertThat(max4.get(), is(55 *55));
		
		DoubleStream doubleStream = DoubleStream.of(-1, -2.718, 3.141, 2.0);
		OptionalDouble maxDouble = doubleStream.max();
		assertThat(maxDouble.getAsDouble(), is(3.141));
		
		
		List<Integer> myList = Arrays.asList(12,2,3,4,5,6,7);
		
		long count = myList.stream().count();
		assertThat(count, is(7L));
		
		Optional<Integer> max2 = myList.stream().max(Integer::compare);
		assertThat(max2.get(), is(12));
		
		List<String> myStringList = Arrays.asList("one", "TWO", "three", "FOUR", "five");
		Optional<String> stringMax = myStringList.stream().max((s1, s2) -> s1.toLowerCase().compareTo(s2.toLowerCase()));
		long count2 = myStringList.stream().count();
		assertThat(count2, is(5L));
		assertThat(stringMax.get(), is("TWO"));
	}
	
	@Test
	public void testAverage() {
		IntStream myStream = IntStream.rangeClosed(1, 10);
		OptionalDouble avg = myStream.average();
		assertThat(avg.getAsDouble(), is(5.5));
		
		Stream<Integer> myStream2 = Stream.of(1, 2, 3, 4, 5);
		OptionalDouble avg2 = myStream2.mapToInt(i -> i).average();
		assertThat(avg2.getAsDouble(), is (3.0));
		
		Stream<Integer> myStream3 = Stream.of(1, 2, 3, 4, 5);
		OptionalDouble avg3 = myStream3.mapToLong(i -> i).average();
		assertThat(avg3.getAsDouble(), is (3.0));
	}
	
	@Test
	public void testSum() {
		IntStream myStream = IntStream.rangeClosed(1, 10);
		int mySum = myStream.sum();
		assertThat(mySum, is(55));
		
		DoubleStream doubleStream = IntStream.rangeClosed(1, 5).asDoubleStream();
		double mySum2 = doubleStream.sum();
		assertThat(mySum2, is(15.0));
		
		DoubleStream doubleStream3 = IntStream.rangeClosed(1, 6).mapToDouble(i -> i);
		double mySum3 = doubleStream3.sum();
		assertThat(mySum3, is(21.0));
	}
	
	@Test(expected = IllegalStateException.class)
	public void testCantReadStreamTwice() {
		Stream<Integer> myStream = Stream.of(1,2,3,4,5,6);
		long count = myStream.count();
		long count2 = myStream.count(); // will fail because Stream has been closed
	}
	
	@Test
	public void testCollect() {
		Stream<Integer> myStream = Stream.of(11, 22, 33, 44);
		List<Integer> myList = myStream.collect(Collectors.toList());
		assertThat(myList.size(), is(4));
	}
	
	@Test
	public void testCollect2() {
		Stream<Integer> myStream = Stream.of(11, 22, 33, 44, 44, 33, 22, 11);
		Set<Integer> mySet = myStream.collect(Collectors.toSet());
		assertThat(mySet.size(), is(4));
	}
	
	@Test
	public void testCollect3() {
		Stream<Integer> myStream = Stream.of(11, 22, 33, 44, 44, 33, 22, 11);
		Double avg = myStream.collect(Collectors.averagingDouble(i -> i));
		assertThat(avg, is(27.5));
	}
	
	@Test
	public void testCollect4() {
		Stream<Integer> myStream = Stream.of(11, 22, 33, 44, 44, 33, 22, 11);
		Double avg = myStream.collect(Collectors.averagingInt(i -> i));
		assertThat(avg, is(27.5));
	}
	
	@Test
	public void testCollect5() {
		Stream<Integer> myStream = Stream.of(11, 22, 33, 44, 44, 33, 22, 11);
		Double avg = myStream.collect(Collectors.averagingLong(i -> i));
		assertThat(avg, is(27.5));
	}
	
	static class Person {
		@Override
		public String toString() {
			return "Person [name=" + name + ", age=" + age + "]";
		}
		final String name;
		final int age;
		Person(String name, int age) {
			this.name = name; this.age = age;
		}
	}
	
	List<Person> people = Arrays.asList(new Person("Billy", 21), new Person("Mary", 37), new Person("James", 21), new Person("John", 45), new Person("Tom", 95), new Person("Roger", 37));
	
	@Test
	public void testCollectGroupingBy() {
		Map<Integer, List<Person>> byAge = people.stream().collect(Collectors.groupingBy(p -> p.age));
		assertThat(byAge.get(37).size(), is(2)); // Mary and Roger
		assertTrue(byAge.get(1) == null); // nobody is aged 1
	}
	
	@Test
	public void testCollectJoining() {
		Stream<String> myStream = Stream.of("good", "bad", "ugly");
		String result = myStream.collect(Collectors.joining("-"));
		assertThat(result, is ("good-bad-ugly"));
	}
	
	@Test
	public void testCollectJoining2() {
		Stream<Person> myStream = people.stream();
		String result = myStream.map(p -> p.name).collect(Collectors.joining("-"));
		assertThat(result, is ("Billy-Mary-James-John-Tom-Roger"));
	}
	
	@Test
	public void testCollectPartitioningBy() {
		Stream<Person> myStream = people.stream();
		Map<Boolean, List<Person>> oldAndYoung = myStream.collect(Collectors.partitioningBy(p -> p.age > 40));
		
		List<Person> youngPeople = oldAndYoung.get(false);
		List<Person> oldPeople = oldAndYoung.get(true);
		
		assertThat(youngPeople.size(), is(4)); // Billy, Mary, James, Roger
		assertThat(oldPeople.size(), is(2)); // John, Tom
	}
	
	//  Develop code that uses Java SE 8 collection improvements, including the Collection.removeIf(), List.replaceAll(), Map.computeIfAbsent(), and Map.computeIfPresent() methods
	@Test
	public void testCollectionRemoveIf() {
		Set<String> mySet = Stream.of("good", "bad", "indifferent", "ugly").collect(Collectors.toSet());
		assertThat(mySet.size(), is(4));
		
		mySet.removeIf(s -> s.equalsIgnoreCase("UGLY"));
		assertThat(mySet.size(), is(3));
		
		Predicate<String> isBad = new Predicate<String>() {
			@Override
			public boolean test(String t) {
				return t.equalsIgnoreCase("good");
			}
		};
		
		mySet.removeIf(isBad);
		assertThat(mySet.size(), is(2));
		
		Predicate<String> isTooLong = (String s) -> s.length() > 4;
		mySet.removeIf(isTooLong);
		assertThat(mySet.size(), is(1));
		
		Predicate<String> anotherPredicate = isBad.and(isTooLong.negate());
		
	}
	
	@Test
	public void testListReplaceAll() {
		List<String> myList = Arrays.asList("france", "germany", "uk", "spain", "portugal", "netherlands", "belgium");
		UnaryOperator<String> isUK = s -> String.valueOf(s.equalsIgnoreCase("UK"));
		myList.replaceAll(isUK);
		assertFalse(myList.get(0).equals("true"));
		assertFalse(myList.get(1).equals("true"));
		assertTrue(myList.get(2).equals("true"));
	}
	
	@Test
	public void testMapComputeIfPresent() {
		Map<Integer, String> myMap = new HashMap<>();
		myMap.put(1, "one");
		myMap.put(3, "three");
		
		assertThat(myMap.size(), is(2));
		
		myMap.computeIfPresent(2, (num, val) -> "WAS " + val);
		assertThat(myMap.size(), is(2));
		assertThat(myMap.get(1), is("one"));
		assertThat(myMap.get(3), is("three"));
		
		myMap.computeIfPresent(3, (num, val) -> "WAS " + val);
		assertThat(myMap.size(), is(2));
		assertThat(myMap.get(1), is("one"));
		assertThat(myMap.get(3), is("WAS three"));
	}
	
	@Test
	public void testMapComputeIfAbsent() {
		Map<Integer, String> myMap = new HashMap<>();
		myMap.put(1, "one");
		myMap.put(3, "three");
		assertThat(myMap.size(), is(2));
		
		myMap.computeIfAbsent(2, num -> "NO " + num);
		assertThat(myMap.size(), is(3));
		assertThat(myMap.get(1), is("one"));
		assertThat(myMap.get(2), is("NO 2"));
		assertThat(myMap.get(3), is("three"));
		
		myMap.computeIfAbsent(3, num -> "NO " + num);
		assertThat(myMap.size(), is(3));
		assertThat(myMap.get(1), is("one"));
		assertThat(myMap.get(2), is("NO 2"));
		assertThat(myMap.get(3), is("three"));
	}
	
	@Test
	public void testMapPutIfAbsent() {
		Map<Integer, String> myMap = new HashMap<>();
		myMap.put(1, "one");
		myMap.put(3, "three");
		assertThat(myMap.size(), is(2));
		
		myMap.putIfAbsent(2, "two");
		
		assertThat(myMap.size(), is(3));
		assertThat(myMap.get(1), is("one"));
		assertThat(myMap.get(2), is("two"));
		assertThat(myMap.get(3), is("three"));
	}
	
	//  Develop  code that uses the merge(), flatMap(), and map() methods on Java Streams
	// TODO what is merge() for Streams????
	@Test
	public void testConcat() {
		
		String [] names = { "Matthew", "Mark", "Luke", "John" };
		String [] names2 = { "Curly", "Moe", "Larry", "John" };
		Stream<String> myStream = Arrays.stream(names);
		Stream<String> myStream2 = Arrays.stream(names2);
		
		List<String> result = Stream.concat(myStream, myStream2).collect(Collectors.toList());
		assertThat(result.get(0), is("Matthew"));
		assertThat(result.get(1), is("Mark"));
		assertThat(result.get(2), is("Luke"));
		assertThat(result.get(3), is("John"));
		assertThat(result.get(4), is("Curly"));
		assertThat(result.get(5), is("Moe"));
		assertThat(result.get(6), is("Larry"));
		assertThat(result.get(7), is("John"));
	}
	
	@Test
	public void testFlatMap() {
		Stream<String> names = Stream.of("Matthew", "Mark", "Luke", "John");
		List<String> result = names.flatMap(s -> Stream.of(s,s,s)).collect(Collectors.toList());
		assertThat(result.size(), is(12));
	}
	
	@Test
	public void testFlatMap2() {
		Stream<String> names = Stream.of("Matthew", "Mark", "Luke", "John");
		List<String> result = names.flatMap(s -> Arrays.asList(s,s,s).stream()).collect(Collectors.toList());
		assertThat(result.size(), is(12));
	}
	
	@Test
	public void testMap() {
		Stream<String> names = Stream.of("Matthew", "Mark", "Luke", "John");
		List<String> result = names.map(s -> s + ":" + s).collect(Collectors.toList());
		assertThat(result.size(), is(4));
		assertThat(result.get(0), is("Matthew:Matthew"));
	}

}
