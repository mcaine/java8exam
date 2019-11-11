package com.mikeycaine.examcode;

import org.junit.Test;

import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

public class ForkJoin {

    @Test
    public void testRecursiveTask() throws ExecutionException, InterruptedException {
        class MyTask extends RecursiveTask<String> {
            @Override
            protected String compute() {
                return "Hello";
            }
        }
        ForkJoinTask<String> result = new ForkJoinPool().submit(new MyTask());
        assertEquals("Hello", result.get());

    }

    @Test
    public void testRecursiveAction() throws ExecutionException, InterruptedException {
        class MyTask extends RecursiveAction {
            @Override
            protected void compute() {
                System.out.println("Well hello there");
            }
        }
       ForkJoinTask<Void> result = new ForkJoinPool().submit(new MyTask());
    }
}
