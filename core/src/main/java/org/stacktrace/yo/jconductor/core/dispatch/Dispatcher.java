package org.stacktrace.yo.jconductor.core.dispatch;

import org.stacktrace.yo.jconductor.core.job.Work;
import org.stacktrace.yo.jconductor.core.job.stage.JobStage;
import org.stacktrace.yo.jconductor.core.job.stage.StageListener;

import java.util.function.Consumer;

public interface Dispatcher {

    <T, V> String schedule(Work<T, V> job, T params);

    <T, V> String schedule(Work<T, V> job, T params, StageListener<V> listener);

    void consume();
}
