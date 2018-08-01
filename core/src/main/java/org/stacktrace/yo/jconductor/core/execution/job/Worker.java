package org.stacktrace.yo.jconductor.core.execution.job;

import org.stacktrace.yo.jconductor.core.execution.stage.JobExecutionStage;
import org.stacktrace.yo.jconductor.core.execution.stage.JobStage;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.work.Job;
import org.stacktrace.yo.jconductor.core.execution.work.Work;
import org.stacktrace.yo.jconductor.core.util.supplier.LazyLoading;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Worker<T, V> {

    protected final LazyLoading<T> params;
    protected final String id;
    protected final StageListener<V> listener;
    protected final Job<T, V> job;
    protected V result;
    protected JobExecutionStage status;

    public Worker(String id, Job<T, V> job, Supplier<T> params) {
        this(id, job, params, new StageListener.NoOpListener<>());
    }

    public Worker(String id, Job<T, V> job, Supplier<T> params, Consumer<JobStage<V>> onStart, Consumer<JobStage<V>> onComplete, Consumer<Throwable> onError) {
        this(id, job, params, new StageListener.DefaultStageListener<>(onStart, onComplete, onError));
    }

    public Worker(String id, Work<T, V> work, Supplier<T> params) {
        this(id, new BasicJob<>(work), params);
    }

    public Worker(String id, Work<T, V> work, Supplier<T> params, StageListener<V> listener) {
        this(id, new BasicJob<>(work), params, listener);
    }

    public Worker(String id, Job<T, V> job, Supplier<T> params, StageListener<V> listener) {
        this.id = id;
        this.job = job;
        this.params = LazyLoading.lazy(params);
        this.listener = listener;
    }

    protected final void consumeStart() {
        this.status = JobExecutionStage.RUNNING;
        this.listener.onStart().accept(this.status.createStage(this.id));
    }

    protected final void consumeComplete() {
        this.status = JobExecutionStage.COMPLETE;
        this.listener.onComplete().accept(this.status.createStage(this.id, this.result));
    }

    protected final void consumeError(Throwable e) {
        this.status = JobExecutionStage.ERRORED;
        this.listener.onError().accept(e);
    }
}
