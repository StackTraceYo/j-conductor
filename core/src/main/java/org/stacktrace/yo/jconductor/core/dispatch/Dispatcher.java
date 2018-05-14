package org.stacktrace.yo.jconductor.core.dispatch;

import org.stacktrace.yo.jconductor.core.job.Job;
import org.stacktrace.yo.jconductor.core.job.stage.StageListener;

public interface Dispatcher {

    <T, V> String schedule(Job<T, V> job, T params);

    <T, V> String schedule(Job<T, V> job, T params, StageListener<V> listener);

    void consume();
}
