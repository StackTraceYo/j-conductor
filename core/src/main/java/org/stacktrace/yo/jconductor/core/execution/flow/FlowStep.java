package org.stacktrace.yo.jconductor.core.execution.flow;

import org.stacktrace.yo.jconductor.core.execution.work.Work;

public class FlowStep<Param, Result> implements Work<Param, Result> {

    @Override
    public Result doWork(Param params) {
        return null;
    }
}
