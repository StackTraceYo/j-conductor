package org.stacktrace.yo.jconductor.core.execution.work;


public interface Job<Param, Result> extends PreStart<Param>, PostRun, Work<Param, Result> {

    @Override
    default void postRun() {
    }

    @Override
    default void init(Param params) {
    }
}

