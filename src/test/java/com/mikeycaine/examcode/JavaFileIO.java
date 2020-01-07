package com.mikeycaine.examcode;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class JavaFileIO {

	// Operate on file and directory paths by using the Paths class

	@Test
	public void testPath() {
		Path path = Paths.get("c:\\Users\\Mike Caine\\My Documents\\qmds.txt");
		System.out.println("\t file name: " + path.getFileName());
		System.out.println("\t root of the path: " + path.getRoot());
		System.out.println("\t parent of the target: " + path.getParent());

		Path p2 = path.getName(0);
		assertThat(p2.toString(), is("Users"));

		Path p3 = path.getName(1);
		assertThat(p3.toString(), is("Mike Caine"));
	}

	// Check, delete, copy, or move a file or directory by using the Files class
	@Test
	public void testFiles() {
		Path path = Paths.get("c:\\Users\\Mike Caine\\My Documents\\qmds.txt");
		assertTrue(Files.exists(path));

		try {
			long size = Files.size(path);
			assertThat(size, is(6101L));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Recursively access a directory tree by using the DirectoryStream and FileVisitor interfaces
	@Test
	public void testDirectoryStream() {
		Path path = Paths.get("c:\\Users\\Mike Caine\\Documents");
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			stream.forEach(f -> System.out.println(f.toString()));
		} catch (DirectoryIteratorException ex) {
			// I/O error encounted during the iteration, the cause is an IOException
			ex.printStackTrace();
			ex.getCause().printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFileVisitor() {

		Path path = Paths.get("c:\\Users\\Mike Caine\\Documents");

		FileVisitor<Path> myVisitor = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
					throws IOException {
				System.out.println("Visiting " + file + "  attrs=" + attrs);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException e)
					throws IOException {
				if (e == null) {
					//return FileVisitResult.CONTINUE;
					System.out.println("Post visit " + dir);
					return FileVisitResult.TERMINATE;
				} else {
					// directory iteration failed
					throw e;
				}
			}
		};

		try {
			Files.walkFileTree(path, myVisitor);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Find a file by using the PathMatcher interface, and use Java SE 8 I/O improvements, including Files.find(), Files.walk(), and lines() methods
	@Test
	public void testPathMatcher() {
		PathMatcher pm = path -> path.endsWith("txt");
		FileSystem fileSystem =  FileSystems.getDefault();
		PathMatcher pathMatcher = fileSystem.getPathMatcher("glob:*.java");
	}

	// Observe the changes in a directory by using the WatchService interface
	@Test
	public void testWatchService() throws IOException, InterruptedException {
		WatchService watchService = FileSystems.getDefault().newWatchService();
		Path path = Paths.get("c:\\Users\\Mike Caine\\Documents");
		path.register(watchService, ENTRY_DELETE);

		//WatchKey key = watchService.take();

	}

}
