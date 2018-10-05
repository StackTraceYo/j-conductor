package org.stacktrace.yo.jconductor.core.execution.job;

import org.stacktrace.yo.jconductor.core.dispatch.work.CompletedWork;
import org.stacktrace.yo.jconductor.core.execution.stage.CompletedJobStage;
import org.stacktrace.yo.jconductor.core.execution.stage.JobExecutionStage;
import org.stacktrace.yo.jconductor.core.execution.stage.JobStage;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.work.Job;
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

    public Worker(String id, Job<T, V> job, Supplier<T> params, Consumer<JobStage<V>> onStart, Consumer<CompletedJobStage<V>> onComplete, Consumer<Throwable> onError) {
        this(id, job, params, new StageListener.DefaultStageListener<>(onStart, onComplete, onError));
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

        this.listener.onComplete().accept(
                new CompletedJobStage<>(
                        this.status,
                        this.id,
                        new CompletedWork<>(
                                result,
                                params.get(),
                                job.getClass().toGenericString(),
                                id
                        )
                )
        );
    }

    protected final void consumeError(Throwable e) {
        this.status = JobExecutionStage.ERRORED;
        this.listener.onError().accept(e);
    }
}
