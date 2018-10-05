package org.stacktrace.yo.jconductor.core.dispatch.work;

import org.stacktrace.yo.jconductor.core.execution.stage.CompletedJobStage;
import org.stacktrace.yo.jconductor.core.execution.stage.JobStage;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListenerBuilder;
import org.stacktrace.yo.jconductor.core.execution.work.Job;
import org.stacktrace.yo.jconductor.core.util.supplier.LazyLoading;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ScheduledWork<T, V> {

    private final Job<T, V> job;
    private final LazyLoading<T> params;
    private final String id;
    private StageListener<V> stageListener;

    public ScheduledWork(Job<T, V> job, Supplier<T> params, String id) {
        this.job = job;
        this.params = LazyLoading.lazy(params);
        this.id = id;
        this.stageListener = new StageListener.NoOpListener<>();
    }

    public ScheduledWork(Job<T, V> job, Supplier<T> params, String id, Consumer<CompletedJobStage<V>> onComplete) {
        this(job, params, id);
        this.stageListener = new StageListenerBuilder<V>().onComplete(onComplete).finish();
    }

    public ScheduledWork(Job<T, V> job, Supplier<T> params, String id, Consumer<CompletedJobStage<V>> onComplete, Consumer<Throwable> onError) {
        this(job, params, id);
        this.stageListener = new StageListenerBuilder<V>()
                .onComplete(onComplete)
                .next()
                .onError(onError)
                .finish();

    }

    public ScheduledWork(Job<T, V> job, Supplier<T> params, String id, Consumer<JobStage<V>> onStart, Consumer<CompletedJobStage<V>> onComplete, Consumer<Throwable> onError) {
        this(job, params, id);
        this.stageListener = new StageListenerBuilder<V>()
                .onStart(onStart)
                .next()
                .onComplete(onComplete)
                .next()
                .onError(onError)
                .finish();
    }

    public ScheduledWork(Job<T, V> job, Supplier<T> params, String id, StageListener<V> listener) {
        this(job, params, id);
        this.stageListener = listener;
    }

    public Consumer<CompletedJobStage<V>> onComplete() {
        return this.stageListener.onComplete();
    }

    public Consumer<JobStage<V>> onStart() {
        return this.stageListener.onStart();
    }

    public Consumer<Throwable> onError() {
        return this.stageListener.onError();
    }

    public Job<T, V> getJob() {
        return job;
    }

    public Supplier<T> getParams() {
        return params;
    }

    public String getId() {
        return id;
    }
}
