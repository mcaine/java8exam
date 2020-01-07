package com.mikeycaine.examcode;

import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Test
    public void testSubmit() {
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinPool pool2 = new ForkJoinPool(2);

        ExecutorService es = pool;
        es.submit(() -> System.out.println("Hello"));

        //ExecutorService service = Executors.newSingleThreadExecutor();
        try (Closeable close = pool::shutdown; Closeable close2 = pool2::shutdown) {
            pool.submit(new ForkJoinTask<String>() {
                @Override
                public String getRawResult() {
                    return "Raw result";
                }

                @Override
                protected void setRawResult(String value) {

                }

                @Override
                protected boolean exec() {
                    return false;
                }
            });

            List<Future<String>> result = pool2.invokeAll(
                    IntStream.rangeClosed(1,10).mapToObj(i -> new Callable<String>() {

                        @Override
                        public String call() throws Exception {
                            String val = String.valueOf(i);
                            return Thread.currentThread().getName() + String.format(" [%s:%s]", val, val);
                        }
                    }).collect(Collectors.toList())
            );

            result.forEach( fut -> {
                try {
                    System.out.println(fut.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });

            pool2.submit(new RecursiveTask<String>() {

                @Override
                protected String compute() {
                    return null;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
