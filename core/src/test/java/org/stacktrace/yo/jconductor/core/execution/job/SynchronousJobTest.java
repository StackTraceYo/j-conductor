package org.stacktrace.yo.jconductor.core.execution.job;

import org.junit.Test;
import org.mockito.Mockito;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListenerBuilder;
import org.stacktrace.yo.jconductor.core.execution.work.Job;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class SynchronousJobTest {

    @Test
    public void synchronousJobCanBeRun() {
        DefaultWorker<String, String> classUnderTest = new DefaultWorker<>("test_id", params -> "Return " + params, "Parameter");
        String result = classUnderTest.run();
        assertEquals("Return Parameter", result);
    }

    @Test
    public void synchronousJobCanBeRunWithBlock() {
        DefaultWorker<String, String> classUnderTest = new DefaultWorker<>("test_id", params -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException("Errored");
            }
            return "Return " + params;
        }, "Parameter");
        String result = classUnderTest.run();
        assertEquals("Return Parameter", result);
    }

    @Test
    public void synchronousJobCanBeRunWithOnCompleteCallBack() {
        List<String> list = new ArrayList<>();
        List<String> spyList = Mockito.spy(list);

        DefaultWorker<String, String> classUnderTest = new DefaultWorker<>("test_id",
                params -> "Return " + params,
                "Parameter",
                new StageListenerBuilder<String>()
                        .onComplete(
                                onComplete -> spyList.add(onComplete.getStageResult())
                        ).build()
        );
        String result = classUnderTest.run();
        Mockito.verify(spyList).add("Return Parameter");
        assertEquals("Return Parameter", result);
        assertEquals(1, spyList.size());
    }

    @Test
    public void synchronousJobHandlesError() {
        List<String> list = new ArrayList<>();
        List<String> spyList = Mockito.spy(list);

        DefaultWorker<String, String> classUnderTest = new DefaultWorker<>("test_id",
                params -> {
                    throw new RuntimeException("Error");
                },
                "Parameter",
                new StageListenerBuilder<String>()
                        .onComplete(
                                onComplete -> spyList.add("Complete Called"))
                        .onError(
                                onError -> spyList.add("Error Called")
                        ).build()
        );
        String result = classUnderTest.run();
        assertNull(result);
        Mockito.verify(spyList).add("Error Called");
        assertEquals(1, spyList.size());
    }

    @Test
    public void synchronousJobCallsOnStart() {
        List<String> list = new ArrayList<>();
        List<String> spyList = Mockito.spy(list);

        DefaultWorker<String, String> classUnderTest = new DefaultWorker<>("test_id",
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
        String result = classUnderTest.run();

        Mockito.verify(spyList).add("test_id");
        Mockito.verify(spyList).add("Return Parameter");
        assertEquals("Return Parameter", result);
        assertEquals(2, spyList.size());

    }

    @Test
    public void synchronousJobCallsInit() {
        List<String> list = new ArrayList<>();
        List<String> spyList = Mockito.spy(list);

        DefaultWorker<String, String> classUnderTest = new DefaultWorker<>("test_id",
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
        String result = classUnderTest.run();

        Mockito.verify(spyList).add("Parameter"); // init
        Mockito.verify(spyList).add("test_id"); // start
        Mockito.verify(spyList).add("Return Parameter"); // complete
        assertEquals("Return Parameter", result);
        assertEquals(3, spyList.size());

    }

    @Test
    public void synchronousJobCallsPostRun() {
        List<String> list = new ArrayList<>();
        List<String> spyList = Mockito.spy(list);

        DefaultWorker<String, String> classUnderTest = new DefaultWorker<>("test_id",
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
        String result = classUnderTest.run();

        Mockito.verify(spyList).add("test_id"); // start
        Mockito.verify(spyList).add("Return Parameter"); // complete
        Mockito.verify(spyList).add("Post Run"); // post
        assertEquals("Return Parameter", result);
        assertEquals(3, spyList.size());

    }

    @Test
    public void synchronousJobCallsPostRunOnError() {
        List<String> list = new ArrayList<>();
        List<String> spyList = Mockito.spy(list);

        DefaultWorker<String, String> classUnderTest = new DefaultWorker<>("test_id",
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
                        .onError(onError -> spyList.add(onError.getMessage())
                        ).build()
        );
        String result = classUnderTest.run();
        assertNull(result);
        Mockito.verify(spyList).add("test_id"); //start
        Mockito.verify(spyList).add("Error"); //error
        Mockito.verify(spyList).add("Post Run"); //post
        assertEquals(3, spyList.size());
    }

}