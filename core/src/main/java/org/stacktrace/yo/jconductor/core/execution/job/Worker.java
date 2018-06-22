package org.stacktrace.yo.jconductor.core.execution.job;

import org.stacktrace.yo.jconductor.core.execution.stage.JobExecutionStage;
import org.stacktrace.yo.jconductor.core.execution.stage.JobStage;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.work.Job;
import org.stacktrace.yo.jconductor.core.execution.work.Work;

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
    protected final Job<T, V> job;

    Worker(String id, Job<T, V> job, T params) {
        this.params = params;
        this.id = id;
        this.job = job;
    }

    Worker(String id, Job<T, V> job, T params, Consumer<JobStage<V>> onStart, Consumer<JobStage<V>> onComplete, Consumer<Throwable> onError) {
        this(id, job, params);
        this.onStart = onStart;
        this.onComplete = onComplete;
        this.onError = onError;
    }

    public Worker(String id, Work<T, V> work, T params) {
        this.params = params;
        this.id = id;
        this.job = new AbstractJob<>(work);
    }

    public Worker(String id, Work<T, V> work, T params, StageListener<V> listener) {
        this(id, new AbstractJob<>(work), params, listener.onStart(), listener.onComplete(), listener.onError());
    }

    Worker(String id, Job<T, V> job, T params, StageListener<V> listener) {
        this(id, job, params, listener.onStart(), listener.onComplete(), listener.onError());
    }

    protected final void consumeStart() {
        this.running = true;
        this.started = true;
        if (this.onStart != null) {
            this.onStart.accept(JobExecutionStage.RUNNING.createStage(this.id));
        }
    }

    protected final void consumeComplete() {
        this.completed = true;
        this.running = false;
        this.started = false;
        if (this.onComplete != null) {
            this.onComplete.accept(JobExecutionStage.COMPLETE.createStage(this.id, this.result));
        }
    }

    protected final void consumeError(Throwable e) {
        this.errored = true;
        this.completed = true;
        this.running = false;
        this.started = false;
        if (this.onError != null) {
            this.onError.accept(e);
        }
    }
}