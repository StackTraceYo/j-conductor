package org.stacktrace.yo.jconductor.core.dispatch.work;

import java.util.Optional;

public class CompletedWork<T, V> {

    private final V result;
    private final Throwable exception;
    private final T params;
    private final String jobName;
    private final String id;

    public CompletedWork(V result, T params, String id, String jobName) {
        this.result = result;
        this.exception = null;
        this.params = params;
        this.jobName = jobName;
        this.id = id;
    }

    public CompletedWork(Throwable exception, T params, String id, String jobName) {
        this.result = null;
        this.exception = exception;
        this.params = params;
        this.jobName = jobName;
        this.id = id;
    }

    public Optional<V> getResult() {
        return Optional.ofNullable(result);
    }

    public Optional<Throwable> getException() {
        return Optional.ofNullable(exception);
    }

    public T getParams() {
        return params;
    }

    public String getJobName() {
        return jobName;
    }

    public String getId() {
        return id;
    }
}
