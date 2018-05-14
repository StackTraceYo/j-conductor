package org.stacktrace.yo.jconductor.core.job;

import org.stacktrace.yo.jconductor.core.job.stage.JobExecutionStage;
import org.stacktrace.yo.jconductor.core.job.stage.JobStage;
import org.stacktrace.yo.jconductor.core.job.stage.StageListener;

import java.util.function.Consumer;

public abstract class Worker<T, V> {

    protected final T params;
    protected final String id;
    protected V result;
    protected boolean running;
    protected boolean completed;
    protected boolean errored;
    protected boolean started;
    protected Consumer<JobStage<V>> onComplete;
    protected Consumer<Throwable> onError;
    protected Consumer<JobStage<V>> onStart;
    protected final Work<T, V> work;

    public Worker(String id, Work<T, V> work, T params) {
        this.params = params;
        this.id = id;
        this.work = work;
    }

    public Worker(String id, Work<T, V> work, T params, Consumer<JobStage<V>> onComplete) {
        this(id, work, params);
        this.onComplete = onComplete;
    }

    public Worker(String id, Work<T, V> work, T params, Consumer<JobStage<V>> onComplete, Consumer<Throwable> onError) {
        this(id, work, params);
        this.onComplete = onComplete;
        this.onError = onError;
    }

    public Worker(String id, Work<T, V> work, T params, Consumer<JobStage<V>> onStart, Consumer<JobStage<V>> onComplete, Consumer<Throwable> onError) {
        this(id, work, params);
        this.onStart = onStart;
        this.onComplete = onComplete;
        this.onError = onError;
    }

    public Worker(String id, Work<T, V> work, T params, StageListener<V> listener) {
        this(id, work, params, listener.onComplete(), listener.onError());
    }

    protected final void consumeStart() {
        this.running = true;
        if (this.onComplete != null) {
            this.onStart.accept(JobExecutionStage.RUNNING.createStage(this.id));
        }
    }

    protected final void consumeComplete() {
        this.completed = true;
        if (this.onComplete != null) {
            this.onComplete.accept(JobExecutionStage.COMPLETE.createStage(this.id, this.result));
        }
    }

    protected final void consumeError(Throwable e) {
        this.errored = true;
        if (this.onError != null) {
            this.onError.accept(e);
        }
    }

    public static final class Builder {

    }
}
