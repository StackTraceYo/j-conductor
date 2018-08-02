package org.stacktrace.yo.jconductor.core.dispatch.dispatcher;

import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.work.Job;
import org.stacktrace.yo.jconductor.core.util.supplier.MultiSupplier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface AsyncDispatcher extends Dispatcher {

    <T, V> CompletableFuture<V> scheduleAsync(Job<T, V> job, Supplier<T> params);

    <T, V> CompletableFuture<V> scheduleAsync(Job<T, V> job, Supplier<T> params, StageListener<V> listener);

    <T, V> CompletableFuture<V> scheduleAsync(Job<T, V> job, MultiSupplier<T> params);

    <T, V> CompletableFuture<V> scheduleAsync(Job<T, V> job, MultiSupplier<T> params, StageListener<V> listener);
}
