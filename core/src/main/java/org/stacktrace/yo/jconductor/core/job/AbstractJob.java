package org.stacktrace.yo.jconductor.core.job;

import org.stacktrace.yo.jconductor.core.job.stage.JobExecutionStage;
import org.stacktrace.yo.jconductor.core.job.stage.JobStage;
import org.stacktrace.yo.jconductor.core.job.stage.StageListener;

import java.util.function.Consumer;

public abstract class AbstractJob<T, V> implements Job<T, V> {

    protected final T params;
    protected final String id;
    protected V result;
    protected boolean running;
    protected boolean completed;
    protected boolean errored;
    protected boolean started;
    protected Consumer<? super JobStage<V>> onComplete;
    protected Consumer<? super Throwable> onError;


    public AbstractJob(String id, T params) {
        this.id = id;
        this.params = params;
        this.running = false;
        this.completed = false;
        this.errored = false;
    }

    public AbstractJob(String id, T params, Consumer<? super JobStage<V>> onComplete) {
        this(id, params);
        this.onComplete = onComplete;
    }

    public AbstractJob(String id, T params, Consumer<? super JobStage<V>> onComplete, Consumer<? super Throwable> onError) {
        this(id, params);
        this.onComplete = onComplete;
        this.onError = onError;
    }

    public AbstractJob(String id, T params, StageListener<V> listener) {
        this(id, params, listener.onComplete(), listener.onError());
    }

    public final V start() {
        this.running = true;
        this.result = this.execute(this.params);
        this.completed = true;
        return this.result;
    }

    protected final void consumeComplete() {
        if (this.onComplete != null) {
            this.onComplete.accept(JobExecutionStage.COMPLETE.createStage(this.id, this.result));
        }
    }

    protected final void consumeError(Throwable e) {
        if (this.onError != null) {
            this.onError.accept(e);
        }
    }

    public T getParams() {
        return params;
    }

    public String getId() {
        return id;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isErrored() {
        return errored;
    }
}
