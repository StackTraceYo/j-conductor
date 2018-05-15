package org.stacktrace.yo.jconductor.core.execution.job;

import org.stacktrace.yo.jconductor.core.execution.stage.JobStage;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.work.*;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class SynchronousJob<T, V> extends Worker<T, V> implements Executable<V> {

    public SynchronousJob(String id, Job<T, V> job, T params) {
        super(id, job, params);
    }

    public SynchronousJob(String id, PreStart<T> pre, Work<T, V> work, T params) {
        super(id, pre, work, params);
    }

    public SynchronousJob(String id, PreStart<T> pre, Work<T, V> work, PostRun post, T params) {
        super(id, pre, work, post, params);
    }

    public SynchronousJob(String id, Work<T, V> work, T params) {
        super(id, work, params);
    }

    public SynchronousJob(String id, Work<T, V> work, T params, Consumer<JobStage<V>> onComplete) {
        super(id, work, params, onComplete);
    }

    public SynchronousJob(String id, Work<T, V> work, T params, Consumer<JobStage<V>> onComplete, Consumer<Throwable> onError) {
        super(id, work, params, onComplete, onError);
    }

    public SynchronousJob(String id, Job<T, V> job, T params, Consumer<JobStage<V>> onComplete) {
        super(id, job, params, onComplete);
    }

    public SynchronousJob(String id, Job<T, V> job, T params, Consumer<JobStage<V>> onComplete, Consumer<Throwable> onError) {
        super(id, job, params, onComplete, onError);
    }

    public SynchronousJob(String id, Job<T, V> job, T params, Consumer<JobStage<V>> onStart, Consumer<JobStage<V>> onComplete, Consumer<Throwable> onError) {
        super(id, job, params, onStart, onComplete, onError);
    }

    public SynchronousJob(String id, Job<T, V> job, T params, StageListener<V> listener) {
        super(id, job, params, listener);
    }

    public V run() {
        job.init(params);
        consumeStart();
        try {
            result = job.doWork(params);
            consumeComplete();
            job.postRun();
            return result;
        } catch (Exception e) {
            this.consumeError(e);
            job.postRun();
            return null;
        }

    }

    public V run(Executor e) {
        return run();
    }

}
