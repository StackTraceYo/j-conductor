package org.stacktrace.yo.jconductor.core.job;

public interface Work<T, V> {

    V doWork(T params);

    default void cleanup() {
        // no op
    }

    default void init() {
        // no op
    }

    default void init(T params) {
        // no op
    }


}
