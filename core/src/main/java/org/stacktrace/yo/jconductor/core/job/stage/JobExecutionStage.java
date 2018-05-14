package org.stacktrace.yo.jconductor.core.job.stage;

public enum JobExecutionStage {
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
