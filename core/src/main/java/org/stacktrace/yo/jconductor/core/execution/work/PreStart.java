package org.stacktrace.yo.jconductor.core.execution.work;

@FunctionalInterface
public interface PreStart<T> {

    void init(T params);

}
