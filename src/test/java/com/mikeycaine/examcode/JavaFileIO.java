package com.mikeycaine.examcode;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.stream.Collectors;

public class JavaFileIO {

	// Operate on file and directory paths by using the Paths class

	@Test
	public void testPath() {
		Path path = Paths.get("c:\\Users\\Mike Caine\\My Documents\\qmds.txt");
		System.out.println("Path is " + path);
		System.out.println("\t file name: " + path.getFileName());
		System.out.println("\t root of the path: " + path.getRoot());
		System.out.println("\t parent of the target: " + path.getParent());


		Path p2 = path.getName(0);
		assertThat(p2.toString(), is("Users"));

		Path p3 = path.getName(1);
		assertThat(p3.toString(), is("Mike Caine"));

		Path p4 = path.getName(2);
		assertThat(p4.toString(), is("My Documents"));

		Path fileName = path.getFileName();
		Path root = path.getRoot();

		Path parent = path.getParent();
	}

	@Test
	public void testPath2() throws MalformedURLException, URISyntaxException {

		URL url = new URL("http://www.website.com/information.asp");
		URI uri = url.toURI();
		Path result = Paths.get(uri);
		System.out.println("result is " + result);
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

	@Test
	public void testDeleteFile() throws IOException {
		Path notThere = Paths.get("c:\\Users\\Mike Caine\\My Documents\\not_there.txt");

		if (Files.exists(notThere)) {
			Files.delete(notThere);
		}

		assertFalse(Files.exists(notThere));

		Writer writer = new FileWriter(notThere.toFile());
		writer.write("Heres some text");
		writer.close();

		assertTrue(Files.exists(notThere));

		Path newName = Paths.get("c:\\Users\\Mike Caine\\deletemeplz.txt");
		assertFalse(Files.exists(newName));

		Files.move(notThere, newName);
		assertFalse(Files.exists(notThere));
		assertTrue(Files.exists(newName));

		Files.delete(newName);
		assertFalse(Files.exists(newName));


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

	@Test
	public void testFilesystemProviders() {
		String result = FileSystemProvider.installedProviders().stream().map(fsp -> fsp.getScheme()).collect(Collectors.joining(", "));
		System.out.println(result); // file, jar
	}

}
