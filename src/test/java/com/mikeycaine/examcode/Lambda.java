package com.mikeycaine.examcode;

import static org.junit.Assert.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

public class Lambda {
	
//  Lambda
//
//  Define and write functional interfaces and describe the interfaces of the java.util.function package
//  Describe a lambda expression; refactor the code that uses an anonymous inner class to use a lambda expression; describe type inference and target typing
//  Develop code that uses the built-in interfaces included in the java.util.function package, such as Function, Consumer, Supplier, UnaryOperator, Predicate, and Optional APIs, including the primitive and binary variations of the interfaces
//  Develop code that uses a method reference, including refactoring a lambda expression to a method reference

	@Test
	public void test() throws InterruptedException {
		Runnable r = () -> System.out.println("HELLO!");
		Thread t = new Thread(r);
		t.start();
		t.join();
		assertTrue(!t.isAlive());
	}
	
//  Use the built-in interfaces included in the java.util.function package such as Predicate, Consumer, Function, and Supplier
	
	
	// Predicate<T> checks a condition and returns a boolean result
	// @FunctionalInterface
	// public interface Predicate<T> {
	//    boolean test(T t);
	// }
	@Test
	public void testPredicate() {
		Predicate<String> nicePredicate = (String s) -> s.toUpperCase().equals("NICE");
		Predicate<String> nastyPredicate = (String s) -> s.toUpperCase().equals("NASTY");
		Predicate<String> isUpperCasePredicate = (String s) -> s.toUpperCase().equals(s);
		Predicate<String> isLowerCasePredicate = (String s) -> s.toLowerCase().equals(s);
		
		List<String> result = (List<String>) Stream.of("Nice", "NASTY", "GOOD", "BAD").filter(nicePredicate).collect(Collectors.toList());
		assertTrue(result.size() == 1);
		assertTrue(result.contains("Nice"));
		
		// Predicate.or()		
		Predicate<String> eitherPredicate = nicePredicate.or(nastyPredicate);
		result = (List<String>) Stream.of("NICE", "NASTY", "GOOD", "BAD").filter(eitherPredicate).collect(Collectors.toList());
		assertTrue(result.size() == 2);
		assertTrue(result.contains("NICE"));
		assertTrue(result.contains("NASTY"));
		
		Predicate<String> allUpperOrAllLower = isUpperCasePredicate.or(isLowerCasePredicate);
		result = (List<String>) Stream.of("NICE", "nasty", "Good", "Bad").filter(allUpperOrAllLower).collect(Collectors.toList());
		assertTrue(result.size() == 2);
		assertTrue(result.contains("NICE"));
		assertTrue(result.contains("nasty"));
		
		// Predicate.and()
		Predicate<String> isLowerCaseNasty = nastyPredicate.and(isLowerCasePredicate);
		result = (List<String>) Stream.of("Nasty", "NASTY", "lower", "nasty", "nice").filter(isLowerCaseNasty).collect(Collectors.toList());
		assertTrue(result.size() == 1);
		assertTrue(result.contains("nasty"));
	}
	
	@Test
	// A Consumer<T> takes an argument of type T and returns nothing
	public void testConsumer() {
		List<String> myList = new ArrayList<>();
		List<String> myOtherList = new ArrayList<>();
		
		Consumer<String> listAppendConsumer = (String s) -> myList.add(s);
		Consumer<String> otherListAppendConsumer = (String s) -> myOtherList.add(s);
		Consumer<String> both = listAppendConsumer.andThen(otherListAppendConsumer);
		
		Stream.of("this", "that", "and", "the", "other").forEach(both);
		assertTrue(myList.size() == 5);
		assertTrue(myOtherList.size() == 5);
		
		Consumer<String> stringPrinter = System.out::println;
		Consumer<String> stringPrinter2 = s -> System.out.println(s);
	}
	
	@Test
	public void testFunction() {
		Integer[] numbers = { 1, -2, 3, -4 };
		Function<Integer, Integer> squareIt = (Integer i) -> i * i;
		Function<Integer, Integer> negative = (Integer i) -> -i;
		
		Function<Integer, Integer> squareAndThenNegative = squareIt.andThen(negative);
		Function<Integer, Integer> negativeAndThenSquare = squareIt.compose(negative);
		
		List<Integer> result = Arrays.stream(numbers).map(squareAndThenNegative).collect(Collectors.toList());
		assertTrue(result.get(0).equals(-1));
		assertTrue(result.get(1).equals(-4));
		
		List<Integer> result2 = Arrays.stream(numbers).map(negativeAndThenSquare).collect(Collectors.toList());
		assertTrue(result2.get(0).equals(1));
		assertTrue(result2.get(1).equals(4));
		
		Function<String, Integer> myStringLength = s -> s.length();
	}
	
	@Test
	public void testSupplier() {
		Supplier<Boolean> booleanSupplier = () -> new Random().nextBoolean();
		
		List<Boolean> result = Stream.generate(booleanSupplier).limit(10).collect(Collectors.toList());
		assertTrue(result.size() == 10);
		
		Boolean b = booleanSupplier.get();
		
		// Constructor has no parameters
		Supplier<String> newString = String::new;
		String s = newString.get();
		assertTrue(s.equals(""));
		
		// Constructor takes a String
		Function<String, Integer> newInteger = Integer::new;
		Integer i = newInteger.apply("-999");
		assertTrue(i.equals(-999));
		
		List<Integer> intList = Stream.of("123","456").map(newInteger).collect(Collectors.toList());
		assertTrue(intList.size() == 2);
		assertTrue(intList.get(0).equals(123));
		assertTrue(intList.get(1).equals(456));
	}
	
	// Develop code that uses primitive versions of functional interfaces
	@Test
	public void testPrimitivePredicates() {
		IntPredicate evenNums = i -> (i % 2) == 0;
		LongPredicate evenLongs = l -> (l % 2) == 0;
		DoublePredicate evenDoubles = d -> (d % 2) == 0;
		DoublePredicate evenDoubles2 = (double d) -> (d % 2) == 0;
		//DoublePredicate evenDoubles3 = (long d) -> (d % 2) == 0;  // doesn't compile
		
		// FloatPredicate doesn't exist
		// FloatPredicate evenFloats = f -> (f % 2) == 0;
		
		IntStream evenStream = IntStream.range(1, 101).filter(evenNums);
		
		List<Integer> evenIntegers = evenStream.boxed().collect(Collectors.toList());
		assertTrue(evenIntegers.size() == 50);
	}
	
	@Test
	public void testPrimitiveFunctions() {
		IntFunction<String> intFunctionString = i -> String.valueOf(i);
		LongFunction<String> longFunctionString = l -> String.valueOf(l);
		DoubleFunction<String> doubleFunctionString = d -> String.valueOf(d);
		
		ToIntFunction<String> stringToInt = s -> s.length();
		ToLongFunction<String> stringToLong = s -> s.length();
		ToDoubleFunction<String> stringToDouble = s-> s.length() + 0.5;
		
		IntToLongFunction intToLong = i -> i + 1L;
		IntToDoubleFunction intToDouble = i -> i + 0.5;
		
		LongToIntFunction longToInt = l -> String.valueOf(l).length();
		LongToDoubleFunction longToDouble = l -> l + 0.5;
		DoubleToIntFunction doubleToInt = d -> (int)Math.floor(d);
		DoubleToLongFunction doubleToLong = d -> (long)Math.floor(d);
	}
	
	@Test
	public void testPrimitiveConsumers() {
		IntConsumer intConsumer = i -> System.out.println(i);
		LongConsumer longConsumer = l -> System.out.println(l);
		DoubleConsumer doubleConsumer = d -> System.out.println(d);
		
		ObjIntConsumer<String> objIntConsumerString = (String s, int i) -> System.out.println(s + ": " + i);
		ObjLongConsumer<String> objLongConsumerString = (String s, long l) -> System.out.println(s + ": " + l);
		ObjDoubleConsumer<String> objDoubleConsumerString = (String s, double d) -> System.out.println(s + ": " + d);
	}
	
	@Test
	public void testPrimitiveSuppliers() {
		BooleanSupplier booleanSupplier = () -> true;
		assertTrue(booleanSupplier.getAsBoolean());
		
		IntSupplier intSupplier = () -> 57;
		assertTrue(intSupplier.getAsInt() == 57);
		
		LongSupplier longSupplier = () -> 123L;
		assertTrue(longSupplier.getAsLong() == 123L);
		
		DoubleSupplier doubleSupplier = () -> Math.PI;
		assertTrue(doubleSupplier.getAsDouble() > 3.141 && doubleSupplier.getAsDouble() < 3.142);
	}
	
	// Develop code that uses binary versions of functional interfaces
	// The functional interfaces BiPredicate, BiConsumer, and BiFunction are binary versions of Predicate, Consumer, and Function respectively.
	// There is no binary equivalent for Supplier since it does not take any arguments. The prefix “Bi” indicates the version that takes “two” arguments.
	@Test
	public void testBinaryFunctions() {
		BiPredicate<String, Long> biPredicate = (s,l) -> s.length() < 5 && l > 100L;
		BiConsumer<String, Double> biConsumer = (s,d) -> System.out.println(s + ": " + d);
		BiFunction<String, Double, Integer> biFunction = (s,d) -> s.length() + (int)Math.floor(d);
		
		boolean b = biPredicate.test("HI", 123456L);
		assertTrue(b);
		
		biConsumer.accept("A double", 31337.747);
		
		int val = biFunction.apply("HELLO", 3.141);
		assertTrue(val == 8);
	}
	
	// Develop code that uses the UnaryOperator interface
	// @FunctionalInterface
	// public interface UnaryOperator<T> extends Function<T, T> {...}
	@Test
	public void testUnaryOperator() {
		UnaryOperator<String> quoteEm = s -> "'" + s + "'";
		
		List<String> myStrings = Arrays.asList("one", "two", "three");
		myStrings.replaceAll(quoteEm);
		
		assertTrue(myStrings.size() == 3);
		assertTrue(myStrings.get(0).equals("'one'"));
		assertTrue(myStrings.get(1).equals("'two'"));
		assertTrue(myStrings.get(2).equals("'three'"));
		
		UnaryOperator<Integer> intAbs = Math::abs;
		
		List<Integer> myIntegers = Arrays.asList(-1,2,-3,4,-5);
		myIntegers.replaceAll(intAbs);
		assertTrue(myIntegers.size() == 5);
		assertTrue(myIntegers.get(0) == 1);

		IntUnaryOperator intOp = i -> -i;
		IntToDoubleFunction i2d = i -> 0.1 + i;
		DoubleToIntFunction d2i = d -> (int)d;

		IntStream.of(1, 2, 3, 420, 69, 31337)
				.map(intOp)
				.mapToDouble(i2d)
				.mapToInt(d2i)
				.forEach(System.out::println);

		LongUnaryOperator longOp = l -> -l;
		DoubleUnaryOperator dblOp = d -> -d;
		
	}

	@Test
	public void testOptional() {
		OptionalLong optLong = OptionalLong.of(31337L);
		OptionalInt optInt = OptionalInt.of(420);
		OptionalDouble optDouble = OptionalDouble.of(Math.E);
	}

}
