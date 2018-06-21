package org.stacktrace.yo.jconductor.core.execution.job;

import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.work.Job;
import org.stacktrace.yo.jconductor.core.execution.work.Work;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

public class AsynchronousJob<T, V> extends Worker<T, V> implements Executable<CompletableFuture<V>> {

    public AsynchronousJob(String id, Job<T, V> job, T params) {
        super(id, job, params);
    }

    public AsynchronousJob(String id, Job<T, V> job, T params, StageListener<V> listener) {
        super(id, job, params, listener);
    }

    public AsynchronousJob(String id, Work<T, V> work, T params) {
        super(id, work, params);
    }

    public AsynchronousJob(String id, Work<T, V> work, T params, StageListener<V> listener) {
        super(id, work, params, listener);
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
