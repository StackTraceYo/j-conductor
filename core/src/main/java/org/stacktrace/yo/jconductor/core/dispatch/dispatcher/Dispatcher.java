package org.stacktrace.yo.jconductor.core.dispatch.dispatcher;

import org.stacktrace.yo.jconductor.core.dispatch.store.ResultStore;
import org.stacktrace.yo.jconductor.core.dispatch.work.CompletedWork;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.work.Job;

import java.util.Optional;

public interface Dispatcher {

    <T, V> String schedule(Job<T, V> job, T params);

    <T, V> String schedule(Job<T, V> job, T params, StageListener<V> listener);

    void consume();

    boolean shutdown();

    Optional<CompletedWork> fetch(String id);

    ResultStore getResultStore();
}
