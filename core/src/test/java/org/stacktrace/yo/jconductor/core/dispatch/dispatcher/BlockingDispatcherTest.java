package org.stacktrace.yo.jconductor.core.dispatch.dispatcher;

import org.junit.Test;
import org.stacktrace.yo.jconductor.core.execution.work.Job;

public class BlockingDispatcherTest {

    private static class TestJob implements Job<String, String> {
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
    }

    private BlockingDispatcher classToTest = new BlockingDispatcher();

    @Test
    public void canSchedule() {
        String id = classToTest.schedule(new TestJob(), "String");
        System.out.println(id);
    }
}