package org.stacktrace.yo.jconductor.core.work;

@FunctionalInterface
public interface Work<T, V> {

    V doWork(T params);

}
