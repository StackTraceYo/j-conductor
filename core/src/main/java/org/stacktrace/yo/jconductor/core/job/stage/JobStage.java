package org.stacktrace.yo.jconductor.core.job.stage;

public class JobStage<V> {

    private final JobExecutionStage stage;
    private final V stageResult;
    private final String id;

    public JobStage(JobExecutionStage stage, String id, V stageResult) {
        this.stage = stage;
        this.stageResult = stageResult;
        this.id = id;
    }

    public JobStage(JobExecutionStage stage, String id) {
        this.stage = stage;
        this.stageResult = null;
        this.id = id;
    }

    public JobExecutionStage getStage() {
        return stage;
    }

    public V getStageResult() {
        return stageResult;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "JobStage{" +
                "stage=" + stage +
                ", stageResult=" + stageResult +
                ", id='" + id + '\'' +
                '}';
    }


}
