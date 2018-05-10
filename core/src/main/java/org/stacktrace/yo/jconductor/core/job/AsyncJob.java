package org.stacktrace.yo.jconductor.core.job;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class AsyncJob<T, V> extends AbstractJob<T, V> {

    public AsyncJob(T params) {
        super(params);
    }

    public CompletableFuture<V> startAsync(String id) {
        this.id = id;
        this.running = true;
        return CompletableFuture.supplyAsync(() -> this.execute(this.params))
                .whenComplete((u, throwable) -> {
                    this.completed = true;
                });
    }

    public CompletableFuture<V> startAsync(String id, Executor e) {
        this.id = id;
        this.running = true;
        return CompletableFuture.supplyAsync(() -> this.execute(this.params), e)
                .whenComplete((u, throwable) -> {
                    this.completed = true;
                });
    }


}
