package com.mikeycaine.examcode;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

public class Futures {
	
	@Test
	public void testCancel() throws InterruptedException {
		Callable<String> c = () -> {
			//try {
				for (int i = 0; i < 1_000_000 && !Thread.currentThread().isInterrupted(); ++i) {
					System.out.println("i is " + i);
					//Thread.sleep(50L);
				}
			//} catch (InterruptedException ex) {
			//	ex.printStackTrace();
			//}
			
			return "Finished";
		};
		
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<String> fut = executor.submit(c);
		Thread.sleep(10L);
		fut.cancel(true);
		Thread.sleep(10000L);
		System.out.println("END");
		
	}

}
