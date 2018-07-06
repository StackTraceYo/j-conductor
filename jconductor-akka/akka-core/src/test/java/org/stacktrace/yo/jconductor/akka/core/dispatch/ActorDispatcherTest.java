package org.stacktrace.yo.jconductor.akka.core.dispatch;

import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ActorDispatcherTest {

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
    public void testReplyWithMatchGroupJoined() throws InterruptedException {
        TestKit probe = new TestKit(system);
        ActorDispatcher dispatcher = new ActorDispatcher(5, "TestDispatcher", system);
        dispatcher.test();
        dispatcher.test();
        dispatcher.test();
        dispatcher.test();
        dispatcher.test();
//        Thread.sleep(5000);
    }


}