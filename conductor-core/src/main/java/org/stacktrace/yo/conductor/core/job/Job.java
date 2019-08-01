package org.stacktrace.yo.conductor.core.job;


public interface Job<Param, Result> {

    default void init(Param params) {
    }

    default void postRun() {
    }

    Result run(Param params);
}

