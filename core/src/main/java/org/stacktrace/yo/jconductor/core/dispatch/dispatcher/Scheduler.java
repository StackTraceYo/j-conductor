package org.stacktrace.yo.jconductor.core.dispatch.dispatcher;

import java.util.concurrent.ScheduledExecutorService;

public interface Scheduler {

    ScheduledExecutorService scheduler();
}
