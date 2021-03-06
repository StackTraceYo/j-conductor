package org.stacktrace.yo.jconductor.core.execution.job;

import org.stacktrace.yo.jconductor.core.execution.stage.CompletedJobStage;
import org.stacktrace.yo.jconductor.core.execution.stage.JobStage;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.work.Job;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FutureWorker<T, V> extends Worker<T, V> implements Executable<CompletableFuture<V>> {


    public FutureWorker(String id, Job<T, V> job, Supplier<T> params) {
        super(id, job, params);
    }

    public FutureWorker(String id, Job<T, V> job, Supplier<T> params, Consumer<JobStage<V>> onStart, Consumer<CompletedJobStage<V>> onComplete, Consumer<Throwable> onError) {
        super(id, job, params, onStart, onComplete, onError);
    }

    public FutureWorker(String id, Job<T, V> job, Supplier<T> params, StageListener<V> listener) {
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
            job.init(params.get());
            this.consumeStart();
            return this.job.doWork(this.params.get());
        });
    }

    private CompletableFuture<V> supplyFuture(Executor e) {
        return CompletableFuture.supplyAsync(() -> {
            job.init(params.get());
            this.consumeStart();
            return this.job.doWork(this.params.get());
        }, e);
    }
}
