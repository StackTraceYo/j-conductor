package org.stacktrace.yo.jconductor.core.execution.work;


public interface Job<T, V> extends PreStart<T>, PostRun, Work<T, V> {
}

