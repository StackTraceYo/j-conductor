package org.stacktrace.yo.jconductor.core.execution.work;

@FunctionalInterface
public interface Work<Param, Result> {

    Result doWork(Param params);

}
