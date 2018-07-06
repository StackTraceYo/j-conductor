package org.stacktrace.yo.jconductor.core.dispatch.dispatcher;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.stacktrace.yo.jconductor.core.dispatch.work.CompletedWork;
import org.stacktrace.yo.jconductor.core.execution.work.Job;
import org.stacktrace.yo.jconductor.core.util.async.Futures;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FutureDispatcherTest {

    private static class TestJob implements Job<String, String> {
        @Override
        public String doWork(String params) {
            return "Return " + params;
        }

        @Override
        public void postRun() {
            System.out.println("Cleanup");
        }

        @Override
        public void init(String params) {
            System.out.println("Params: " + params);
        }
    }

    private static class SlowTestJob implements Job<String, String> {
        private final long delay;

        public SlowTestJob(long delay) {
            this.delay = delay;
        }

        @Override
        public String doWork(String params) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
            }
            return "Return " + params;
        }

        @Override
        public void postRun() {
            System.out.println("Cleanup");
        }

        @Override
        public void init(String params) {
            System.out.println("Params: " + params);
        }
    }

    private FutureDispatcher classToTest;

    @Test
    public void canScheduleAndRetrieve() {
        classToTest = new FutureDispatcher();
        String id = classToTest.schedule(new TestJob(), "String");
        assertNotNull(id);
        classToTest.shutdown();
        CompletedWork result = classToTest.getResultStore().getResult(id).get();
        assertTrue(result.getResult().isPresent());
        assertEquals(result.getResult().get(), "Return String");
    }

    @Test
    public void canScheduleAndRetrieveFuture() {
        classToTest = new FutureDispatcher();
        String result = classToTest.scheduleAsync(new TestJob(), "String").join();
        assertEquals(result, "Return String");
    }


    @Test
    public void canScheduleMultipleAndRetrieve() {
        classToTest = new FutureDispatcher();
        List<CompletableFuture<String>> futures = Lists.newArrayList(
                classToTest.scheduleAsync(new TestJob(), "String"),
                classToTest.scheduleAsync(new TestJob(), "String2"),
                classToTest.scheduleAsync(new SlowTestJob(2000), "String3"),
                classToTest.scheduleAsync(new TestJob(), "String4"),
                classToTest.scheduleAsync(new SlowTestJob(2000), "String5")
        );

        List<String> results = Futures.toFutureList(futures)
                .join();

        assertEquals(results.get(0), "Return String");
        assertEquals(results.get(1), "Return String2");
        assertEquals(results.get(2), "Return String3");
        assertEquals(results.get(3), "Return String4");
        assertEquals(results.get(4), "Return String5");
    }

    @Test
    public void canScheduleMultipleAndRetrieveConcurrent() {
        classToTest = new FutureDispatcher(5);
        List<CompletableFuture<String>> futures = Lists.newArrayList(
                classToTest.scheduleAsync(new TestJob(), "String"),
                classToTest.scheduleAsync(new TestJob(), "String2"),
                classToTest.scheduleAsync(new SlowTestJob(2000), "String3"),
                classToTest.scheduleAsync(new TestJob(), "String4"),
                classToTest.scheduleAsync(new SlowTestJob(2000), "String5")
        );

        List<String> results = Futures.toFutureList(futures)
                .join();

        assertEquals(results.get(0), "Return String");
        assertEquals(results.get(1), "Return String2");
        assertEquals(results.get(2), "Return String3");
        assertEquals(results.get(3), "Return String4");
        assertEquals(results.get(4), "Return String5");
    }

    @Test
    public void canScheduleMultipleAndRetrieveConcurrent2() {
        classToTest = new FutureDispatcher(5);
        List<String> results = Lists.newArrayList();
        List<CompletableFuture<String>> futures = Lists.newArrayList(
                classToTest.scheduleAsync(new SlowTestJob(3000), "String")
                        .thenApply(s -> {
                            results.add(s);
                            return s;
                        }),
                classToTest.scheduleAsync(new TestJob(), "String2")
                        .thenApply(s -> {
                            results.add(s);
                            return s;
                        }),
                classToTest.scheduleAsync(new SlowTestJob(1000), "String3")
                        .thenApply(s -> {
                            results.add(s);
                            return s;
                        }),
                classToTest.scheduleAsync(new TestJob(), "String4")
                        .thenApply(s -> {
                            results.add(s);
                            return s;
                        }),
                classToTest.scheduleAsync(new SlowTestJob(2000), "String5")
                        .thenApply(s -> {
                            results.add(s);
                            return s;
                        })
        );

        CompletableFuture<Void> x = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        while (!x.isDone()) {
        }
        assertEquals(results.get(0), "Return String2");
        assertEquals(results.get(1), "Return String4");
        assertEquals(results.get(2), "Return String3");
        assertEquals(results.get(3), "Return String5");
        assertEquals(results.get(4), "Return String");

    }

    @Test
    public void canScheduleMultipleAndRetrieveWithConcurrency() {
        classToTest = new FutureDispatcher(3);

        String id = classToTest.schedule(new TestJob(), "String");
        String id2 = classToTest.schedule(new TestJob(), "String2");
        String id3 = classToTest.schedule(new SlowTestJob(2000), "String3");
        String id4 = classToTest.schedule(new TestJob(), "String4");
        String id5 = classToTest.schedule(new SlowTestJob(2000), "String5");

        assertNotNull(id);
        assertNotNull(id2);
        assertNotNull(id3);
        assertNotNull(id4);
        assertNotNull(id5);

        classToTest.shutdown();

        CompletedWork result = classToTest.getResultStore().getResult(id).get();
        CompletedWork result2 = classToTest.getResultStore().getResult(id2).get();
        CompletedWork result3 = classToTest.getResultStore().getResult(id3).get();
        CompletedWork result4 = classToTest.getResultStore().getResult(id4).get();
        CompletedWork result5 = classToTest.getResultStore().getResult(id5).get();

        assertTrue(result.getResult().isPresent());
        assertEquals(result.getResult().get(), "Return String");

        assertTrue(result2.getResult().isPresent());
        assertEquals(result2.getResult().get(), "Return String2");

        assertTrue(result3.getResult().isPresent());
        assertEquals(result3.getResult().get(), "Return String3");

        assertTrue(result4.getResult().isPresent());
        assertEquals(result4.getResult().get(), "Return String4");

        assertTrue(result5.getResult().isPresent());
        assertEquals(result5.getResult().get(), "Return String5");
    }
}