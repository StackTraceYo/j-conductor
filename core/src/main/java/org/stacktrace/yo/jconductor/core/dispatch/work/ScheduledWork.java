package org.stacktrace.yo.jconductor.core.dispatch.work;

import org.stacktrace.yo.jconductor.core.execution.stage.JobStage;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListenerBuilder;
import org.stacktrace.yo.jconductor.core.execution.work.Job;

import java.util.function.Consumer;

public class ScheduledWork<T, V> {

    private final Job<T, V> job;
    private final T params;
    private final String id;
    private StageListener<V> stageListener;

    public ScheduledWork(Job<T, V> job, T params, String id) {
        this.job = job;
        this.params = params;
        this.id = id;
    }

    public ScheduledWork(Job<T, V> job, T params, String id, Consumer<JobStage<V>> onComplete) {
        this(job, params, id);
        this.stageListener = new StageListenerBuilder<V>()
                .onComplete(onComplete)
                .build();

    }

    public ScheduledWork(Job<T, V> job, T params, String id, Consumer<JobStage<V>> onComplete, Consumer<Throwable> onError) {
        this(job, params, id);
        this.stageListener = new StageListenerBuilder<V>()
                .onComplete(onComplete)
                .onError(onError)
                .build();

    }

    public ScheduledWork(Job<T, V> job, T params, String id, Consumer<JobStage<V>> onStart, Consumer<JobStage<V>> onComplete, Consumer<Throwable> onError) {
        this(job, params, id);
        this.stageListener = new StageListenerBuilder<V>()
                .onStart(onStart)
                .onComplete(onComplete)
                .onError(onError)
                .build();
    }

    public ScheduledWork(Job<T, V> job, T params, String id, StageListener<V> listener) {
        this(job, params, id);
        this.stageListener = listener;
    }


    public StageListener<V> getListener() {
        return this.stageListener;
    }

    public Job<T, V> getJob() {
        return job;
    }

    public T getParams() {
        return params;
    }

    public String getId() {
        return id;
    }
}
