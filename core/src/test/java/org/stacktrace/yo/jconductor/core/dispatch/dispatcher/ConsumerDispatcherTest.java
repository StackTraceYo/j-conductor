package org.stacktrace.yo.jconductor.core.dispatch.dispatcher;

import org.junit.Test;
import org.stacktrace.yo.jconductor.core.dispatch.work.CompletedWork;
import org.stacktrace.yo.jconductor.core.execution.work.Job;

import static org.junit.Assert.*;

public class ConsumerDispatcherTest {

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
        @Override
        public String doWork(String params) {
            try {
                Thread.sleep(2000);
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

    private ConsumerDispatcher classToTest;

    @Test
    public void canScheduleAndRetrieve() {
        classToTest = new ConsumerDispatcher();
        String id = classToTest.schedule(new TestJob(), "String");
        assertNotNull(id);
        classToTest.shutdown();
        CompletedWork result = classToTest.getResultStore().getResult(id).get();
        assertTrue(result.getResult().isPresent());
        assertEquals(result.getResult().get(), "Return String");
    }


    @Test
    public void canScheduleMultipleAndRetrieve() {
        classToTest = new ConsumerDispatcher();

        String id = classToTest.schedule(new TestJob(), "String");
        String id2 = classToTest.schedule(new TestJob(), "String2");
        String id3 = classToTest.schedule(new SlowTestJob(), "String3");
        String id4 = classToTest.schedule(new TestJob(), "String4");
        String id5 = classToTest.schedule(new SlowTestJob(), "String5");
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

    @Test
    public void canScheduleMultipleAndRetrieveWithConcurrency() {
        classToTest = new ConsumerDispatcher(3);

        String id = classToTest.schedule(new TestJob(), "String");
        String id2 = classToTest.schedule(new TestJob(), "String2");
        String id3 = classToTest.schedule(new SlowTestJob(), "String3");
        String id4 = classToTest.schedule(new TestJob(), "String4");
        String id5 = classToTest.schedule(new SlowTestJob(), "String5");

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