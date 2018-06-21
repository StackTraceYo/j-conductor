package org.stacktrace.yo.jconductor.core.execution.job;

import org.junit.Test;
import org.mockito.Mockito;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListenerBuilder;
import org.stacktrace.yo.jconductor.core.execution.work.Job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class AsynchronousJobTest {

    @Test
    public void asynchronousJobCanBeRun() throws Exception {
        AsynchronousJob<String, String> classUnderTest = new AsynchronousJob<>("test_id", params -> "Return " + params, "Parameter");
        String result = classUnderTest.run().get();
        assertEquals("Return Parameter", result);
    }

    @Test
    public void asynchronousJobCanBeRunWithBlock() throws Exception {
        AsynchronousJob<String, String> classUnderTest = new AsynchronousJob<>("test_id", params -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException("Errored");
            }
            return "Return " + params;
        }, "Parameter");
        String result = classUnderTest.run().get();
        assertEquals("Return Parameter", result);
    }

    @Test
    public void asynchronousJobCanBeRunWithOnCompleteCallBack() throws Exception {
        List<String> list = new ArrayList<>();
        List<String> spyList = Mockito.spy(list);

        AsynchronousJob<String, String> classUnderTest = new AsynchronousJob<>("test_id",
                params -> "Return " + params,
                "Parameter",
                new StageListenerBuilder<String>()
                        .onComplete(
                                onComplete -> spyList.add(onComplete.getStageResult())
                        ).build()
        );
        String result = classUnderTest.run().get();
        Mockito.verify(spyList).add("Return Parameter");
        assertEquals("Return Parameter", result);
        assertEquals(1, spyList.size());
    }

    @Test
    public void asynchronousJobHandlesError() throws Exception {
        List<String> list = new ArrayList<>();
        List<String> spyList = Mockito.spy(list);

        AsynchronousJob<String, String> classUnderTest = new AsynchronousJob<>("test_id",
                params -> {
                    throw new RuntimeException("Error");
                },
                "Parameter",
                new StageListenerBuilder<String>()
                        .onComplete(
                                onComplete -> spyList.add("Complete Called"))
                        .onError(
                                onError -> spyList.add("Error Called"))
                        .build()
        );
        String result = classUnderTest.run().get();
        assertNull(result);
        Mockito.verify(spyList).add("Error Called");
        assertEquals(1, spyList.size());
    }

    @Test
    public void asynchronousJobCallsOnStart() throws Exception {
        List<String> list = new ArrayList<>();
        List<String> spyList = Mockito.spy(list);

        AsynchronousJob<String, String> classUnderTest = new AsynchronousJob<>("test_id",
                new Job<String, String>() {
                    @Override
                    public String doWork(String params) {
                        return "Return " + params;
                    }

                    @Override
                    public void postRun() {

                    }

                    @Override
                    public void init(String params) {

                    }
                },
                "Parameter",
                new StageListenerBuilder<String>()
                        .onStart(onStart -> spyList.add(onStart.getId()))
                        .onComplete(onComplete -> spyList.add(onComplete.getStageResult()))
                        .onError(onError -> spyList.add(onError.getMessage())
                        ).build()
        );
        String result = classUnderTest.run().get();

        Mockito.verify(spyList).add("test_id");
        Mockito.verify(spyList).add("Return Parameter");
        assertEquals("Return Parameter", result);
        assertEquals(2, spyList.size());

    }

    @Test
    public void asynchronousJobCallsInit() throws Exception {
        List<String> list = new ArrayList<>();
        List<String> spyList = Mockito.spy(list);

        AsynchronousJob<String, String> classUnderTest = new AsynchronousJob<>("test_id",
                new Job<String, String>() {
                    @Override
                    public String doWork(String params) {
                        return "Return " + params;
                    }

                    @Override
                    public void postRun() {

                    }

                    @Override
                    public void init(String params) {
                        spyList.add(params);
                    }
                },
                "Parameter",
                new StageListenerBuilder<String>()
                        .onStart(onStart -> spyList.add(onStart.getId()))
                        .onComplete(onComplete -> spyList.add(onComplete.getStageResult()))
                        .onError(onError -> spyList.add(onError.getMessage())
                        ).build()
        );
        String result = classUnderTest.run().get();

        Mockito.verify(spyList).add("Parameter"); // init
        Mockito.verify(spyList).add("test_id"); // start
        Mockito.verify(spyList).add("Return Parameter"); // complete
        assertEquals("Return Parameter", result);
        assertEquals(3, spyList.size());

    }

    @Test
    public void asynchronousJobCallsPostRun() throws Exception {
        List<String> list = new ArrayList<>();
        List<String> spyList = Mockito.spy(list);

        AsynchronousJob<String, String> classUnderTest = new AsynchronousJob<>("test_id",
                new Job<String, String>() {
                    @Override
                    public String doWork(String params) {
                        return "Return " + params;
                    }

                    @Override
                    public void postRun() {
                        spyList.add("Post Run");
                    }

                    @Override
                    public void init(String params) {
                    }
                },
                "Parameter",
                new StageListenerBuilder<String>()
                        .onStart(onStart -> spyList.add(onStart.getId()))
                        .onComplete(onComplete -> spyList.add(onComplete.getStageResult()))
                        .onError(onError -> spyList.add(onError.getMessage())
                        ).build()
        );
        String result = classUnderTest.run().get();

        Mockito.verify(spyList).add("test_id"); // start
        Mockito.verify(spyList).add("Return Parameter"); // complete
        Mockito.verify(spyList).add("Post Run"); // post
        assertEquals("Return Parameter", result);
        assertEquals(3, spyList.size());

    }

    @Test
    public void asynchronousJobCallsPostRunOnError() throws Exception {
        List<String> list = new ArrayList<>();
        List<String> spyList = Mockito.spy(list);

        AsynchronousJob<String, String> classUnderTest = new AsynchronousJob<>("test_id",
                new Job<String, String>() {
                    @Override
                    public String doWork(String params) {
                        throw new RuntimeException("Error");
                    }

                    @Override
                    public void postRun() {
                        spyList.add("Post Run");
                    }

                    @Override
                    public void init(String params) {
                    }
                },
                "Parameter",
                new StageListenerBuilder<String>()
                        .onStart(onStart -> spyList.add(onStart.getId()))
                        .onComplete(onComplete -> spyList.add(onComplete.getStageResult()))
                        .onError(onError -> spyList.add("On Error")
                        ).build()
        );
        String result = classUnderTest.run().get();
        assertNull(result);
        Mockito.verify(spyList).add("test_id"); //start
        Mockito.verify(spyList).add("On Error"); //error
        Mockito.verify(spyList).add("Post Run"); //post
        assertEquals(3, spyList.size());
    }

    @Test
    public void asynchronousJobsCanBeRun() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<String> list = new ArrayList<>();
        List<String> spyList = Mockito.spy(list);

        AsynchronousJob<String, String> classUnderTest = new AsynchronousJob<>("test_id", params -> {
            String result = Thread.currentThread().getName() + " " + params;
            spyList.add(result);
            return result;
        }, "Parameter");
        AsynchronousJob<String, String> classUnderTest2 = new AsynchronousJob<>("test_id2", params -> {
            String result = Thread.currentThread().getName() + " " + params;
            spyList.add(result);
            return result;
        }, "Parameter2");
        CompletableFuture.allOf(new CompletableFuture[]{classUnderTest.run(executorService), classUnderTest2.run(executorService)})
                .join();
        Mockito.verify(spyList).add("pool-1-thread-1 Parameter");
        Mockito.verify(spyList).add("pool-1-thread-2 Parameter2");
        assertEquals(2, spyList.size());
        executorService.shutdown();
    }
}