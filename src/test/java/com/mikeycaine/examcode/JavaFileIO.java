package com.mikeycaine.examcode;

import static org.junit.Assert.*;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaFileIO {

	@Test
	public void testPath() {
		Path path = Paths.get("c:\\Users\\Mike Caine\\My Documents\\qmds.txt");
		System.out.println("\t file name: " + path.getFileName());
		System.out.println("\t root of the path: " + path.getRoot());
		System.out.println("\t parent of the target: " + path.getParent());

	}

}
