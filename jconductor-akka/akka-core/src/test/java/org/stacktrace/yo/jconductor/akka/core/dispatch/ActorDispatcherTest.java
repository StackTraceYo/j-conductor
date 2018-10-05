package org.stacktrace.yo.jconductor.akka.core.dispatch;

import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.stacktrace.yo.jconductor.core.execution.work.Job;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ActorDispatcherTest {

    public static class TestJob implements Job<String, String> {
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

    public static class SlowTestJob implements Job<String, String> {
        @Override
        public String doWork(String params) {
            try {
                Thread.sleep(3000);
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

    private static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testDispatcher() throws InterruptedException {
        ActorDispatcher dispatcher = new ActorDispatcher(5, "TestDispatcher", system);
        String id = dispatcher.schedule(new TestJob(), "Test");
        assertNotNull(id);
    }

    @Test
    public void testDispatcherMultiple() throws InterruptedException {
        ActorDispatcher dispatcher = new ActorDispatcher(5, "TestDispatcher", system);
        String id = dispatcher.schedule(new TestJob(), "Test");
        String id2 = dispatcher.schedule(new TestJob(), "Test2");
        String id3 = dispatcher.schedule(new TestJob(), "Test3");
        String id4 = dispatcher.schedule(new TestJob(), "Test4", null);
        String id5 = dispatcher.schedule(new TestJob(), "Test5");
        assertNotNull(id);
        assertNotNull(id2);
        assertNotNull(id3);
        assertNotNull(id4);
        assertNotNull(id5);
    }

    @Test
    public void testDispatcherAwait() throws InterruptedException {
        ActorDispatcher dispatcher = new ActorDispatcher(5, "TestDispatcher", system);
        String id = dispatcher.scheduleAndWait(new TestJob(), "Test");
        assertNotNull(id);
    }

    @Test
    public void testDispatcherMultipleAwait() throws InterruptedException {
        ActorDispatcher dispatcher = new ActorDispatcher(5, "TestDispatcher", system);
        String id = dispatcher.scheduleAndWait(new TestJob(), "Test");
        String id2 = dispatcher.scheduleAndWait(new TestJob(), "Test2");
        String id3 = dispatcher.scheduleAndWait(new TestJob(), "Test3");
        String id4 = dispatcher.scheduleAndWait(new TestJob(), "Test4");
        String id5 = dispatcher.scheduleAndWait(new TestJob(), "Test5");
        assertNotNull(id);
        assertNotNull(id2);
        assertNotNull(id3);
        assertNotNull(id4);
        assertNotNull(id5);
    }

    @Test
    public void testDispatcherStatus() throws InterruptedException {
        ActorDispatcher dispatcher = new ActorDispatcher(5, "TestDispatcher", system);
        dispatcher.schedule(new SlowTestJob(), "Test");
        dispatcher.schedule(new SlowTestJob(), "Test2");
        dispatcher.schedule(new SlowTestJob(), "Test3");
        dispatcher.schedule(new SlowTestJob(), "Test4");
        dispatcher.schedule(new SlowTestJob(), "Test5");
        assertEquals(0 ,dispatcher.getStatus().pending());
        assertEquals(5 ,dispatcher.getStatus().running());
    }


}