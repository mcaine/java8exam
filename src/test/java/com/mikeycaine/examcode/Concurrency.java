package com.mikeycaine.examcode;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

public class Concurrency {

	// Use classes from the java.util.concurrent package including CyclicBarrier and CopyOnWriteArrayList with a focus on the advantages over and differences from the traditional java.util collections

	//Use Lock, ReadWriteLock, and ReentrantLock classes in the java.util.concurrent.locks and java.util.concurrent.atomic packages to support lock-free thread-safe programming on single variables


	@Test
	public void testLockInterface() {
		Lock myLock = new Lock() {
			@Override
			public void lock() { }

			@Override
			public void lockInterruptibly() throws InterruptedException { }

			@Override
			public boolean tryLock() { return false; }

			@Override
			public boolean tryLock(long time, TimeUnit unit) throws InterruptedException { return false; }

			@Override
			public void unlock() { }

			@Override
			public Condition newCondition() { return null; }
		};
	}

	@Test public void testLockImplementations() {
		Lock lock = new ReentrantLock();

		StampedLock stampedLock = new StampedLock();
		Lock readLock = stampedLock.asReadLock();
		Lock writeLock = stampedLock.asWriteLock();

		ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		Lock readLock2 = readWriteLock.readLock();
		Lock writeLock2 = readWriteLock.writeLock();

	}

	@Test
	public void testBlockingQueues() {
		ArrayBlockingQueue<String> abq = new ArrayBlockingQueue<>(100);
		LinkedBlockingDeque<String> lbd = new LinkedBlockingDeque<>();
		LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<>();

		abq.add("red");
		abq.offer("blue");
		abq.add("green");
		abq.offer("purple");

		List<String> result = abq.stream().collect(Collectors.toList());
		System.out.println(result); //[red, blue, green, purple]

		lbd.add("red");
		lbd.push("blue");
		lbd.add("green");
		lbd.push("purple");

		result = lbd.stream().collect(Collectors.toList());
		System.out.println(result); // [purple, blue, red, green]
	}

	@Test
	public void testLinkedTransferQueue() {
		LinkedTransferQueue<String> linkedTransferQueue = new LinkedTransferQueue<>();
		BlockingQueue<String> blockingQueue = linkedTransferQueue;
		TransferQueue<String> transferQueue = linkedTransferQueue;
		Queue queue = linkedTransferQueue;
	}

	//Use Executor, ExecutorService, Executors, Callable, and Future to execute tasks using thread pools
	@Test
	public void testExecutor() {
		Executor ex = Executors.newCachedThreadPool();
		ex.execute(()->System.out.println("Hello"));
	}

	@Test
	public void testCallableCanThrowCheckedException() {

		class MyCallable implements Callable<Void> {
			@Override
			public Void call() throws Exception {
				throw new IOException("Something");
				//return null;
			}
		}

		ExecutorService es = Executors.newSingleThreadExecutor();
		Future<Void> result = es.submit(new MyCallable());
		try {
			System.out.println("Result " + result.get());
		} catch (ExecutionException  e) {
			System.out.println("Caught an ExecutionException: ");
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Caught an InterruptedException: ");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	@Test
	public void testExecutionService() throws ExecutionException, InterruptedException {
		ExecutorService es = Executors.newSingleThreadExecutor();
		Future<String> future = es.submit(() -> "Christmas");
		String result = future.get(); // blocking
		System.out.println("It's nearly " + result);

		Future<Long> futLong = es.submit(() -> System.out.println("I'm working"), 747L);
		Long longResult = futLong.get();
		System.out.println("I got " + longResult);

		List<Future<String>> futString = es.invokeAll(Arrays.asList(
				() -> "Phineas",
				() -> "Franklin",
				() -> "Freddy"
		));

		futString.forEach(	fut -> {
			String got = null;
			try {
				got = fut.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			System.out.println("Freak: " + got);
		});

	}

	//Use the parallel Fork/Join Framework



	// Create worker threads using Runnable, Callable, and use an ExecutorService to concurrently execute tasks
	@Test
	public void testRunnable() throws InterruptedException {
		final List<String> myStrings = new ArrayList<>();
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
		class Waiter {

			final private String id;
			final private Semaphore semaphore;

			Waiter(String id, Semaphore semaphore) {
				this.id = id;
				this.semaphore = semaphore;
			}

			public Callable<String> threadInfo() {
				return () -> {
					System.out.println(id + " getting semaphore...");
					semaphore.acquire();
					System.out.println("..." + id + " got semaphore...");
					Thread.sleep(20L);
					System.out.println(id + " releasing semaphore");
					semaphore.release();
					return id + " -> " + ThreadLocalRandom.current().nextInt(1000) + ", " + Thread.currentThread().getName();
				};
			}
		}

		Semaphore sem = new Semaphore(1);
		List<Callable<String>> callables = IntStream.range(1, 10).mapToObj(Integer::new)
				.map(i ->  new Waiter("ID" + i, sem).threadInfo())
				.collect(Collectors.toList());

		ExecutorService es = Executors.newFixedThreadPool(10);

		List<Future<String>> results = callables.stream().map( c -> es.submit(c)).collect(Collectors.toList());

		results.forEach(res -> {
			try {
				res.get();
			} catch (InterruptedException| ExecutionException e) {
				e.printStackTrace();
			}
		});

		try {
			results.forEach(res -> {
				try {
					String result = res.get();
					System.out.println("Got result " + result);
				} catch (InterruptedException | ExecutionException ex ) {
					ex.printStackTrace();
				}
			});
		} catch (Exception ie) {
			System.out.println(ie.getMessage());
		}

		es.shutdown();
		try {
			es.awaitTermination(6, TimeUnit.SECONDS );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Done");


	}



	// CountDownLatch allows one or more threads to wait for a countdown to complete.
	@Test
	public void testCountDownLatch() {
		CountDownLatch cdl = new CountDownLatch(3);

		try {
			boolean result = cdl.await(1, TimeUnit.NANOSECONDS);
			assertFalse(result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCountDownLatch2() {
		CountDownLatch cdl = new CountDownLatch(100);

		ExecutorService es = Executors.newFixedThreadPool(10);
		IntStream.rangeClosed(1,100).forEach(i -> es.submit(() -> {
			System.out.println(Thread.currentThread().getName() + " counting down...");
			cdl.countDown();
		}));

		try {
			cdl.await();
			System.out.println("Count is " + cdl.getCount());
			assertEquals(0, cdl.getCount());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}

		es.shutdown();
		try {
			es.awaitTermination(6, TimeUnit.SECONDS );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("DONE");
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
		Phaser phaser = new Phaser();
		int phase = phaser.getPhase();
		assertEquals(0, phase);
	}

	// java.lang.IllegalStateException: Attempted arrival of unregistered party
	@Test(expected = IllegalStateException.class)
	public void testPhaserUnregistered() {
		Phaser phaser = new Phaser();
		phaser.arrive();
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


