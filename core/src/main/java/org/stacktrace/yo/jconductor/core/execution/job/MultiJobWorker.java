package org.stacktrace.yo.jconductor.core.execution.job;

import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.work.Job;
import org.stacktrace.yo.jconductor.core.execution.work.Work;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class MultiJobWorker<T, V> extends Worker<T, V> implements Executable<V> {

    public MultiJobWorker(String id, Work<T, V> work, Supplier<T> params) {
        super(id, work, params);
    }

    public MultiJobWorker(String id, Job<T, V> job, Supplier<T> params) {
        super(id, job, params);
    }

    public MultiJobWorker(String id, Job<T, V> job, Supplier<T> params, StageListener<V> listener) {
        super(id, job, params, listener);
    }

    public MultiJobWorker(String id, Work<T, V> work, Supplier<T> params, StageListener<V> listener) {
        super(id, work, params, listener);
    }

    public V run() {
        job.init(params.get());
        consumeStart();
        try {
            result = job.doWork(params.get());
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
