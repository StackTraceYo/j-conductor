package org.stacktrace.yo.jconductor.core.execution.job;

import org.stacktrace.yo.jconductor.core.execution.stage.JobStage;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.work.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class AsynchronousJob<T, V> extends Worker<T, V> implements Executable<CompletableFuture<V>> {

    public AsynchronousJob(String id, Job<T, V> job, T params) {
        super(id, job, params);
    }

    public AsynchronousJob(String id, PreStart<T> pre, Work<T, V> work, T params) {
        super(id, pre, work, params);
    }

    public AsynchronousJob(String id, PreStart<T> pre, Work<T, V> work, PostRun post, T params) {
        super(id, pre, work, post, params);
    }

    public AsynchronousJob(String id, Work<T, V> work, T params) {
        super(id, work, params);
    }

    public AsynchronousJob(String id, Work<T, V> work, T params, Consumer<JobStage<V>> onComplete) {
        super(id, work, params, onComplete);
    }

    public AsynchronousJob(String id, Job<T, V> job, T params, Consumer<JobStage<V>> onComplete) {
        super(id, job, params, onComplete);
    }

    public AsynchronousJob(String id, Job<T, V> job, T params, Consumer<JobStage<V>> onComplete, Consumer<Throwable> onError) {
        super(id, job, params, onComplete, onError);
    }

    public AsynchronousJob(String id, Job<T, V> job, T params, Consumer<JobStage<V>> onStart, Consumer<JobStage<V>> onComplete, Consumer<Throwable> onError) {
        super(id, job, params, onStart, onComplete, onError);
    }

    public AsynchronousJob(String id, Work<T, V> work, T params, Consumer<JobStage<V>> onComplete, Consumer<Throwable> onError) {
        super(id, work, params, onComplete, onError);
    }

    public AsynchronousJob(String id, Job<T, V> job, T params, StageListener<V> listener) {
        super(id, job, params, listener);
    }

    public CompletableFuture<V> run() {
        return supplyFuture()
                .handle(finish());
    }

    public CompletableFuture<V> run(Executor e) {
        return supplyFuture(e)
                .handle(finish());
    }

    private BiFunction<V, Throwable, V> finish() {
        return (v, throwable) -> {
            V result = null;
            if (throwable != null) {
                this.consumeError(throwable);
            } else {
                this.result = v;
                result = v;
                this.consumeComplete();
            }
            this.job.postRun();
            return result;
        };
    }

    private CompletableFuture<V> supplyFuture() {
        return CompletableFuture.supplyAsync(() -> {
            job.init(params);
            this.consumeStart();
            return this.job.doWork(this.params);
        });
    }

    private CompletableFuture<V> supplyFuture(Executor e) {
        return CompletableFuture.supplyAsync(() -> {
            job.init(params);
            this.consumeStart();
            return this.job.doWork(this.params);
        }, e);
    }
}
