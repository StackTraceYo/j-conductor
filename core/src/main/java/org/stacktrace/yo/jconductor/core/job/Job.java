package org.stacktrace.yo.jconductor.core.job;

public interface Job<T, V> {


    V execute(T params);


}
