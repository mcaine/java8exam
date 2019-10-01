package com.mikeycaine.examcode;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Set;

import static org.hamcrest.core.Is.*;

import org.junit.Test;

public class LanguageEnhancements {
	
//  Language Enhancements
//
//  Develop code that uses String objects in the switch statement, binary literals, and numeric literals, including underscores in literals
//  Develop code that uses try-with-resources statements, including using classes that implement the AutoCloseable interface
//  Develop code that handles multiple Exception types in a single catch block
//  Use static and default methods of an interface including inheritance rules for a default method

	@Test
	public void testStringSwitch() {
		final String myName = "Mike";
		switch(myName) {
			case "Fran": System.out.println("No it's not Fran"); fail(); break;
			case "Mike": System.out.println("Yes that's right!"); break;
			default:
		}
	}
	
	@Test
	public void testEnumSwitch() {
		final MyEnum size = MyEnum.SMALL;
		switch(size) {
			case SMALL: System.out.println("Yes that's right!");  break;
			case MEDIUM: System.out.println("NO! WRONG!"); fail(); break;
			case LARGE: System.out.println("NO! WRONG!"); fail(); break;
			case ENORMOUS: System.out.println("NO! WRONG!"); fail(); break;
			default:
		}
	}
	
	@Test
	public void testEnumSet() {
		Set<MyEnum> sizes = EnumSet.of(MyEnum.SMALL, MyEnum.MEDIUM);
		assertThat(sizes.size(), is(2));
		
		Set<MyEnum> moreSizes = EnumSet.noneOf(MyEnum.class);
		assertThat(moreSizes.size(), is(0));
		
		// won't compile
		//Set<?> stuff = EnumSet.of(MyEnum.SMALL, MyColours.RED);
	}
	
	@Test
	public void testBinaryLiterals() {
		int myVal = 0b111; // 1 + 2 + 4 = 7
		assertThat(myVal, is(7));
		
		int myVal2 = 0B1111; // 1 + 2 + 4 + 8 = 15
		assertThat(myVal2, is(15));
		
		int myVal3 = 0B1_1_1_1;
		assertThat(myVal3, is(15));
	}
	
	@Test
	public void testOctalLiterals() {
		int myVal = 0111; // 1 + 8 + 64 = 73
		assertThat(myVal, is(73));
		
		int myVal2 = 0_7_1;
		assertThat(myVal2, is(57));
	}
	
	@Test
	public void testUnderscoreInLiterals() {
		int myVal = 1_000_000;
		assertThat(myVal, is(1000000));
		
		int myVal2 = 1_0_0_0_0_0_0;
		assertThat(myVal2, is(1000000));
		
		double myVal3 = 1_000_000.23456;
		assertThat(myVal3, is(1000000.23456));
		
		// underscores have to be located within digits
		//double myVal4 = 1_000_000_.23456; // invalid
		double myVal5 = 1000000.23_4_5_6;
		assertThat(myVal5, is(1000000.23456));
		
		// underscores have to be located within digits
		//double myVal6 = 100._0123; // invalid
		
		double myVal6 = 0_0_0_0_0_0_0.0_0;
		assertThat(myVal6, is(0.0));
		
		 int number1 = 0b0111;
		 int number2 = 0111_000;
		 
		 int f1 = 1;
		 int f2 = f1 * 8;
		 int f3 = f2 * 8;
		 int f4 = f3 * 8;
		 int f5 = f4 * 8;
		 int f6 = f5 * 8;
		 
		 int something = f6 + f5 + f4;
		 System.out.println("Something is " + something);
		 
		 assertThat(number1, is(7));
		 assertThat(number2, is(something));
		
	}
	
	@Test
	public void testScientificNotation() {
		double myVal = 1E6;
		assertThat(myVal, is(1000000.0));
		
		double myVal2 = 1.5E6;
		assertThat(myVal2, is(1500000.0));
	}
	
	@Test
	public void testAutoCloseable() throws FileNotFoundException, IOException {
		String path = "src/main/resources/ResourceBundle.properties";
		String result = null;
		String finalResult = null;
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			result = br.readLine();
		} finally {
			finalResult = "done";
		}
		assertThat(result, is("Greeting=Hello"));
		assertThat(finalResult, is("done"));
		
		
		BufferedReader br2 = null;
		String result2 = null;
		// br2 cannot be resolved to a type
		//try (br2 = new BufferedReader(new FileReader(path))) {  // won't compile
		//	result2 = br2.readLine();
		//}
	}
	
	@Test
	public void testMultipleExceptionTypes() throws Exception {
		String path = "src/main/resources/ResourceBundle.properties";
		String result = null;
		String finalResult = null;
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			result = br.readLine();
		
		// won't compile - The exception FileNotFoundException is already caught by the alternative IOException
		//} catch (IOException | FileNotFoundException e) { e.printStackTrace();
			
		// won't compile - The exception FileNotFoundException is already caught by the alternative IOException	
		//} catch (FileNotFoundException | IOException e) { e.printStackTrace();
		
		} finally {
			finalResult = "done";
		}
		assertThat(result, is("Greeting=Hello"));
		assertThat(finalResult, is("done"));
	}
	
	

}

enum MyEnum {SMALL, MEDIUM, LARGE, ENORMOUS};
enum MyColours {RED, GREEN, BLUE, YELLOW, ORANGE};

