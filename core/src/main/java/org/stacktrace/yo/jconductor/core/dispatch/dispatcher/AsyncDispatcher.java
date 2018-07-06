package org.stacktrace.yo.jconductor.core.dispatch.dispatcher;

import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.work.Job;

import java.util.concurrent.CompletableFuture;

public interface AsyncDispatcher extends Dispatcher {

    <T, V> CompletableFuture<V> scheduleAsync(Job<T, V> job, T params);

    <T, V> CompletableFuture<V> scheduleAsync(Job<T, V> job, T params, StageListener<V> listener);

}
