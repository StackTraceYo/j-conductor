package org.stacktrace.yo.jconductor.core.job;

import org.stacktrace.yo.jconductor.core.job.stage.JobStage;
import org.stacktrace.yo.jconductor.core.job.stage.StageListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AsynchronousJob<T, V> extends Worker<T, V> implements Job<CompletableFuture<V>> {


    public AsynchronousJob(String id, Work<T, V> work, T params) {
        super(id, work, params);
    }

    public AsynchronousJob(String id, Work<T, V> work, T params, Consumer<JobStage<V>> onComplete) {
        super(id, work, params, onComplete);
    }

    public AsynchronousJob(String id, Work<T, V> work, T params, Consumer<JobStage<V>> onComplete, Consumer<Throwable> onError) {
        super(id, work, params, onComplete, onError);
    }

    public AsynchronousJob(String id, Work<T, V> work, T params, Consumer<JobStage<V>> onStart, Consumer<JobStage<V>> onComplete, Consumer<Throwable> onError) {
        super(id, work, params, onStart, onComplete, onError);
    }

    public AsynchronousJob(String id, Work<T, V> work, T params, StageListener<V> listener) {
        super(id, work, params, listener);
    }

    public CompletableFuture<V> run() {
        return supplyFuture()
                .whenComplete(finish());
    }

    public CompletableFuture<V> run(Executor e) {
        return supplyFuture(e)
                .whenComplete(finish());
    }

    private BiConsumer<V, Throwable> finish() {
        return (v, throwable) -> {
            if (throwable != null) {
                this.consumeError(throwable);
            } else {
                this.consumeComplete();
            }
            this.work.cleanup();
        };
    }

    private CompletableFuture<V> supplyFuture() {
        return CompletableFuture.supplyAsync(() -> {
            this.consumeStart();
            return this.work.doWork(this.params);
        });
    }

    private CompletableFuture<V> supplyFuture(Executor e) {
        return CompletableFuture.supplyAsync(() -> {
            this.consumeStart();
            return this.work.doWork(this.params);
        }, e);
    }
}
