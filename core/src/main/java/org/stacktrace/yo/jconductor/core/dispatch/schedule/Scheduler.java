package org.stacktrace.yo.jconductor.core.dispatch.schedule;

import java.util.concurrent.ScheduledExecutorService;

public interface Scheduler {

    ScheduledExecutorService scheduler();
}
