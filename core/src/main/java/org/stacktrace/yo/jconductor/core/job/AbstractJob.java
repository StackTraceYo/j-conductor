package org.stacktrace.yo.jconductor.core.job;

import org.stacktrace.yo.jconductor.core.work.PostRun;
import org.stacktrace.yo.jconductor.core.work.PreStart;
import org.stacktrace.yo.jconductor.core.work.Work;

public class AbstractJob<T, V> implements Job<T, V> {

    private final PreStart<T> preStart;
    private final Work<T, V> work;
    private final PostRun post;

    public AbstractJob(PreStart<T> preStart, Work<T, V> work, PostRun post) {
        this.preStart = preStart;
        this.work = work;
        this.post = post;
    }

    public AbstractJob(Work<T, V> work) {
        this.work = work;
        this.post = null;
        this.preStart = null;
    }

    public AbstractJob(Work<T, V> work, PostRun post) {
        this.work = work;
        this.post = post;
        this.preStart = null;
    }

    public AbstractJob(PreStart<T> preStart, Work<T, V> work) {
        this.preStart = preStart;
        this.work = work;
        this.post = null;
    }


    @Override
    public void postRun() {
        if (post != null) {
            post.postRun();
        }
    }

    @Override
    public void init(T params) {
        if (preStart != null) {
            preStart.init(params);
        }
    }

    @Override
    public V doWork(T params) {
        return work.doWork(params);
    }
}
