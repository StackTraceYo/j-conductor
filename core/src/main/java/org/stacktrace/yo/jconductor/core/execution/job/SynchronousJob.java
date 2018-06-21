package org.stacktrace.yo.jconductor.core.execution.job;

import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.work.Job;
import org.stacktrace.yo.jconductor.core.execution.work.Work;

import java.util.concurrent.Executor;

public class SynchronousJob<T, V> extends Worker<T, V> implements Executable<V> {

    public SynchronousJob(String id, Work<T, V> work, T params) {
        super(id, work, params);
    }

    public SynchronousJob(String id, Job<T, V> job, T params) {
        super(id, job, params);
    }

    public SynchronousJob(String id, Job<T, V> job, T params, StageListener<V> listener) {
        super(id, job, params, listener);
    }

    public SynchronousJob(String id, Work<T, V> work, T params, StageListener<V> listener) {
        super(id, work, params, listener);
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
