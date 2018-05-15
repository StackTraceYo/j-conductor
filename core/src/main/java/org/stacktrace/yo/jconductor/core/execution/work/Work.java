package org.stacktrace.yo.jconductor.core.execution.work;

@FunctionalInterface
public interface Work<T, V> {

    V doWork(T params);

}
