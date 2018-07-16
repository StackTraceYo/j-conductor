package org.stacktrace.yo.remote.orch.core;

import org.stacktrace.yo.jconductor.core.dispatch.dispatcher.ConsumerDispatcher;
import org.stacktrace.yo.jconductor.core.dispatch.dispatcher.Dispatcher;

public class Orchestrator {

    private final Dispatcher dispatcher;


    public Orchestrator() {
        dispatcher = new ConsumerDispatcher(10);
    }


}
