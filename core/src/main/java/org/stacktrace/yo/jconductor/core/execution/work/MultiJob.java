package org.stacktrace.yo.jconductor.core.execution.work;


import java.util.Collection;

public interface MultiJob<Param, Result> extends Job<Param, Result> {

    void initAll(Collection<Param> params);

}

