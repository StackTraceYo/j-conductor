package org.stacktrace.yo.jconductor.core.execution.job;

import org.stacktrace.yo.jconductor.core.execution.work.Job;
import org.stacktrace.yo.jconductor.core.execution.work.PostRun;
import org.stacktrace.yo.jconductor.core.execution.work.PreStart;
import org.stacktrace.yo.jconductor.core.execution.work.Work;

public class BasicJob<Param, Result> implements Job<Param, Result> {

    private final PreStart<Param> preStart;
    private final Work<Param, Result> work;
    private final PostRun post;

    public BasicJob(PreStart<Param> preStart, Work<Param, Result> work, PostRun post) {
        this.preStart = preStart;
        this.work = work;
        this.post = post;
    }

    public BasicJob(Work<Param, Result> work) {
        this.work = work;
        this.post = null;
        this.preStart = null;
    }

    public BasicJob(Work<Param, Result> work, PostRun post) {
        this.work = work;
        this.post = post;
        this.preStart = null;
    }

    public BasicJob(PreStart<Param> preStart, Work<Param, Result> work) {
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
    public void init(Param params) {
        if (preStart != null) {
            preStart.init(params);
        }
    }

    @Override
    public Result doWork(Param params) {
        return work.doWork(params);
    }
}
