package org.stacktrace.yo.jconductor.core.execution.job;

import java.util.concurrent.Executor;

public interface Executable<V> {

    V run();

    V run(Executor e);
}
