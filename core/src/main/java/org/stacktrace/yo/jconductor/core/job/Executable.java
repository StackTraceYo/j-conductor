package org.stacktrace.yo.jconductor.core.job;

import java.util.concurrent.Executor;

public interface Executable<V> {

    V run();

    V run(Executor e);
}
