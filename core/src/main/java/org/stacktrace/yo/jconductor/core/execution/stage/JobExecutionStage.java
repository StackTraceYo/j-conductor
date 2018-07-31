package org.stacktrace.yo.jconductor.core.execution.stage;

public enum JobExecutionStage {
    STAGED,
    INITIALZING,
    RUNNING,
    COMPLETE,
    ERRORED;

    public <T> JobStage<T> createStage(String id, T value) {
        return new JobStage<T>(this, id, value);
    }

    public <T> JobStage<T> createStage(String id) {
        return new JobStage<>(this, id);
    }
}
