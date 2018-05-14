package org.stacktrace.yo.jconductor.core.work;

@FunctionalInterface
public interface PreStart<T> {

    void init(T params);

}
