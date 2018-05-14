package org.stacktrace.yo.jconductor.core.dispatch;

import org.stacktrace.yo.jconductor.core.job.Work;
import org.stacktrace.yo.jconductor.core.job.stage.JobStage;

import java.util.function.Consumer;

public class ScheduledWork<T, V> {

    private final Work<T, V> work;
    private final T params;
    private final String id;
    private Consumer<JobStage<V>> onStart;
    private Consumer<JobStage<V>> onComplete;
    private Consumer<Throwable> onError;

    public ScheduledWork(Work<T, V> work, T params, String id) {
        this.work = work;
        this.params = params;
        this.id = id;
    }

    public ScheduledWork(Work<T, V> work, T params, String id, Consumer<JobStage<V>> onStart, Consumer<JobStage<V>> onComplete, Consumer<Throwable> onError) {
        this(work, params, id);
        this.onStart = onStart;
        this.onComplete = onComplete;
        this.onError = onError;
    }

    public Work<T, V> getWork() {
        return work;
    }

    public T getParams() {
        return params;
    }

    public String getId() {
        return id;
    }
}
