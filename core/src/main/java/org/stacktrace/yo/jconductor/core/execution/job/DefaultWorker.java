package org.stacktrace.yo.jconductor.core.execution.job;

import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.work.Job;
import org.stacktrace.yo.jconductor.core.execution.work.Work;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class DefaultWorker<T, V> extends Worker<T, V> implements Executable<V> {

    public DefaultWorker(String id, Work<T, V> work, Supplier<T> params) {
        super(id, work::doWork, params);
    }

    public DefaultWorker(String id, Job<T, V> job, Supplier<T> params) {
        super(id, job, params);
    }

    public DefaultWorker(String id, Job<T, V> job, Supplier<T> params, StageListener<V> listener) {
        super(id, job, params, listener);
    }

    public V run() {
        try {
            job.init(params.get());
            consumeStart();
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
