package com.mikeycaine.examcode;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.junit.Test;

public class Concurrency {
	// Create worker threads using Runnable, Callable, and use an ExecutorService to concurrently execute tasks
	@Test
	public void testRunnable() throws InterruptedException {
		final List<String> myStrings = new ArrayList<String>();
		Runnable r = () -> { myStrings.add("Hello!"); };
		Thread t = new Thread(r);
		t.start();
		t.join();
		
		assertTrue(myStrings.get(0).equals("Hello!"));
	}
	
	@Test
	public void testCallable() throws InterruptedException, ExecutionException {
		Callable<String> c = () -> { return Thread.currentThread().getName(); };
		ExecutorService es = Executors.newSingleThreadExecutor();
		Future<String> fut = es.submit(c);
		String result = fut.get();
		assertTrue(result.contains("thread"));
	}
	
	public class WorkerThreadFactory implements ThreadFactory {
		private int counter = 0;
		private String prefix = "";

		public WorkerThreadFactory(String prefix) {
			this.prefix = prefix;
		}

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, prefix + "-" + counter++);
		}
	}
	
	@Test
	public void testExecutorService() {
		ExecutorService es = Executors.newFixedThreadPool(5);
		ExecutorService es2 = Executors.newCachedThreadPool();
		ExecutorService es3 = Executors.newWorkStealingPool();
		
		ThreadFactory threadFactory = new WorkerThreadFactory("myPrefix"); 
		ExecutorService es4 = Executors.newCachedThreadPool(threadFactory);
		
		ExecutorService es5 = new ForkJoinPool();
		
	}
	
	// Identify potential threading problems among deadlock, starvation, livelock, and race conditions
	
	// Use synchronized keyword and java.util.concurrent.atomic package to control the order of thread execution
	@Test
	public void testAtomicInteger() {
		AtomicInteger ai = new AtomicInteger();
		assertTrue(ai.incrementAndGet() == 1);
		
		AtomicInteger ai2 = new AtomicInteger(55);
		assertTrue(ai2.incrementAndGet() == 56);
		
		assertTrue(ai2.get() == 56);
		
		ai.set(123);
		assertTrue(ai.get() == 123);
		
		AtomicInteger ai3 = new AtomicInteger();
		assertTrue(ai3.getAndSet(57) == 0);
		assertTrue(ai3.get() == 57);
	}
	
	@Test
	public void testAtomicBoolean() {
		AtomicBoolean ab = new AtomicBoolean();
		assertFalse(ab.get());
		
		AtomicBoolean ab2 = new AtomicBoolean(true);
		assertTrue(ab2.get());
	}
	
	@Test
	public void testAtomicIntegerArray() {
		AtomicIntegerArray aia = new AtomicIntegerArray(10); // parameter is length of array
		assertTrue(aia.get(1) == 0);
		
		aia.set(2, 747);
		assertTrue(aia.getAndSet(2, 31337) == 747);
		assertTrue(aia.get(2) == 31337);
		
		// doesn't exist, obv
		//aia.setAndGet(56); 
	}
	
	@Test
	public void testAtomicReference() {
		AtomicReference<String> ars = new AtomicReference<>("hiya");
		assertTrue(ars.getAndSet("bye").equals("hiya"));
		assertTrue(ars.get().equals("bye"));
	}
	
	// Use java.util.concurrent collections and classes including CyclicBarrier and CopyOnWriteArrayList
	
	// A Semaphore controls access to shared resources. A semaphore maintains a counter to specify number of resources that the semaphore controls.
	@Test
	public void testSemaphore() {
		
	}
	
	// CountDownLatch allows one or more threads to wait for a countdown to complete.
	@Test
	public void testCountDownLatch() {
		
	}
	
	// The Exchanger class is meant for exchanging data between two threads. This class is useful when two threads need to synchronize between each other and continuously exchange data.
	@Test
	public void testExchanger() {
		
	}
	
	// CyclicBarrier helps provide a synchronization point where threads may need to wait at a predefined execution point until all other threads reach that point.
	class Process implements Runnable {
		final CyclicBarrier cyclicBarrier;
		boolean isReady = false;
		boolean isStarted = false;
		
		Process(CyclicBarrier barrier) {
			cyclicBarrier = barrier;
		}
		
		@Override
		public void run() {
			isReady = true;
			try {
				cyclicBarrier.await();
			} catch (BrokenBarrierException | InterruptedException ex) {
				assertTrue(false);
			}
			isStarted = true;
		}
	}
	@Test
	public void testCyclicBarrier() throws InterruptedException {
		CyclicBarrier barrier = new CyclicBarrier(2, () -> System.out.println("all ready"));
		
		Process p1 = new Process(barrier);
		assertFalse(p1.isReady);
		assertFalse(p1.isStarted);
		
		new Thread(p1).start();
		Thread.sleep(50L);
		
		assertTrue(p1.isReady);
		assertFalse(p1.isStarted);
		
		Process p2 = new Process(barrier);
		assertFalse(p2.isReady);
		assertFalse(p2.isStarted);
		
		new Thread(p2).start();
		Thread.sleep(50L);
		
		assertTrue(p1.isReady);
		assertTrue(p1.isStarted);
		
		assertTrue(p2.isReady);
		assertTrue(p2.isStarted);
	}
	
	// Phaser is a useful feature when few independent threads have to work in phases to complete a task.
	@Test
	public void testPhaser() {
		
	}
	
	@Test
	public void testCopyOnWriteArrayList() {
        List<String> myList = new CopyOnWriteArrayList<>();
        myList.add("one");
        myList.add("two");
        myList.add("three");
        
        List<String> result = new ArrayList<>();

        Iterator<String> listIter = myList.iterator();
        while(listIter.hasNext()) {
            result.add(listIter.next());
            myList.add("four");
        }
        
        assertTrue(result.size() == 3);
        
        assertTrue(myList.size() == 6);
		
	}

	// Use parallel Fork/Join Framework
	@Test
	public void testForkJoin() {
		
	}
	
	// Use parallel Streams including reduction, decomposition, merging processes, pipelines, and performance
	@Test
	public void testStreams() {
		
	}
	
	@Test
	public void testCompletableFuture() throws InterruptedException, ExecutionException {
		Supplier<String> threadNamer = () -> {
			try {
				Thread.sleep(500L + new Random().nextInt(500));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return Thread.currentThread().getName();
		};
		
		ExecutorService exec = Executors.newCachedThreadPool();
		//ExecutorService exec = Executors.newSingleThreadExecutor();
		
		long started = System.currentTimeMillis();
		
		CompletableFuture<String> fut1 = CompletableFuture.supplyAsync(threadNamer, exec);
		CompletableFuture<String> fut2 = CompletableFuture.supplyAsync(threadNamer, exec);
		CompletableFuture<String> fut3 = CompletableFuture.supplyAsync(threadNamer, exec);
		
		//CompletableFuture<Void> fut3 = fut1.acceptEitherAsync(fut2, s -> System.out.println("Done! Got: " + s)).thenAccept(s -> System.out.println("And then " + s));
		//CompletableFuture<Void> fut3 = fut1.acceptEither(fut2, s -> System.out.println("Done! Got: " + s));
		//CompletableFuture<Void> fut3 = fut1.thenAcceptBothAsync(fut2, (s1,  s2) -> System.out.println("Got " + s1 + " and " + s2 + ", I'm " + Thread.currentThread().getName()));
		CompletableFuture<?> fut4 = fut1.thenCompose((String s) -> { System.out.println("s = " + s);  return fut3;} );
		
		System.out.println(fut4.get());
		
		long finished = System.currentTimeMillis();
		System.out.printf("Took %d%n", (finished - started));
		
	}
}
