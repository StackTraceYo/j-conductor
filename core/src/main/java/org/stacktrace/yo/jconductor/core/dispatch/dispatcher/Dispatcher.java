package org.stacktrace.yo.jconductor.core.dispatch.dispatcher;

import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.work.Job;
import org.stacktrace.yo.jconductor.core.util.supplier.MultiSupplier;

import java.util.function.Supplier;

public interface Dispatcher {

    <T, V> String schedule(Job<T, V> job, StageListener<V> listener);

    <T, V> String schedule(Job<T, V> job, Supplier<T> params);

    <T, V> String schedule(Job<T, V> job, Supplier<T> params, StageListener<V> listener);

    <T, V> String schedule(Job<T, V> job, MultiSupplier<T> params);

    <T, V> String schedule(Job<T, V> job, MultiSupplier<T> params, StageListener<V> listener);

    boolean shutdown();
}
