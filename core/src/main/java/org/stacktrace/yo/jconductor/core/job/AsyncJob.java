package org.stacktrace.yo.jconductor.core.job;

import org.stacktrace.yo.jconductor.core.job.stage.JobStage;
import org.stacktrace.yo.jconductor.core.job.stage.StageListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AsyncJob<T, V> extends AbstractJob<T, V> {


    public AsyncJob(String id, T params) {
        super(id, params);
    }

    public AsyncJob(String id, T params, Consumer<? super JobStage<V>> onComplete) {
        this(id, params);
        this.onComplete = onComplete;
    }

    public AsyncJob(String id, T params, Consumer<? super JobStage<V>> onComplete, Consumer<? super Throwable> onError) {
        this(id, params);
        this.onComplete = onComplete;
        this.onError = onError;
    }

    public AsyncJob(String id, T params, StageListener<V> listener) {
        this(id, params, listener.onComplete(), listener.onError());
    }

    protected final CompletableFuture<V> startAsync() {
        this.running = true;
        return supplyFuture()
                .whenComplete(finish());
    }

    protected final CompletableFuture<V> startAsync(Executor e) {
        this.running = true;
        return supplyFuture(e)
                .whenComplete(finish());
    }

    private BiConsumer<? super V, ? super Throwable> finish() {
        return (v, throwable) -> {
            if (throwable != null) {
                this.consumeError(throwable);
                this.errored = true;
            } else {
                this.consumeComplete();
                this.completed = true;
            }
            this.cleanup();
        };
    }


    protected final CompletableFuture<V> supplyFuture() {
        return CompletableFuture.supplyAsync(() -> this.execute(this.params));
    }

    protected final CompletableFuture<V> supplyFuture(Executor e) {
        return CompletableFuture.supplyAsync(() -> this.execute(this.params), e);
    }
}
