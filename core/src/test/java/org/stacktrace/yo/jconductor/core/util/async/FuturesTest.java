package org.stacktrace.yo.jconductor.core.util.async;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class FuturesTest {

    private ExecutorService executorService;


    @Before
    public void setUp() throws Exception {
        executorService = Executors.newFixedThreadPool(5,
                new ThreadFactoryBuilder()
                        .setNameFormat("TestExecutor-%d")
                        .build());
    }

    @After
    public void tearDown() throws Exception {
        executorService.shutdown();
    }


    @Test
    public void singleFutureFromMultiple() {
        List<String> orderedExecution = Lists.newArrayList();

        CompletableFuture<String> one = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
            orderedExecution.add("finished 1");
            return "Test Future 1";
        }, executorService);

        CompletableFuture<String> two = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException ignored) {
            }
            orderedExecution.add("finished 2");
            return "Test Future 2";
        }, executorService);

        CompletableFuture<String> three = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
            orderedExecution.add("finished 3");
            return "Test Future 3";
        }, executorService);

        CompletableFuture<String> four = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            orderedExecution.add("finished 4");
            return "Test Future 4";
        }, executorService);


        // 4 observables on different threads all complete on different threads when converted to futures
        List<CompletableFuture<String>> futures = Lists.newArrayList(one, two, three, four);

        //Creates a single future of all finished futures
        List<String> joined = Futures.toFutureList(futures)
                .join();
        assertEquals(4, joined.size());
        // submission order
        assertEquals(Lists.newArrayList("Test Future 1", "Test Future 2", "Test Future 3", "Test Future 4"), joined);
        // response order
        assertEquals(Lists.newArrayList("finished 4", "finished 3", "finished 2", "finished 1"), orderedExecution);
    }
}


