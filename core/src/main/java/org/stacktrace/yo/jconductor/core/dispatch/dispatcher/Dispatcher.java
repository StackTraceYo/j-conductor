package org.stacktrace.yo.jconductor.core.dispatch.dispatcher;

import org.stacktrace.yo.jconductor.core.dispatch.work.CompletedWork;
import org.stacktrace.yo.jconductor.core.execution.work.Job;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;

public interface Dispatcher {

    <T, V> String schedule(Job<T, V> job, T params);

    <T, V> String schedule(Job<T, V> job, T params, StageListener<V> listener);

    void consume();

    CompletedWork fetch(String id);

    boolean shutdown();
}
