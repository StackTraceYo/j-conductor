package org.stacktrace.yo.jconductor.core.execution.stage;

import org.stacktrace.yo.jconductor.core.dispatch.work.CompletedWork;

public class CompletedJobStage<V> extends JobStage<CompletedWork<?, V>> {

    public CompletedJobStage(JobExecutionStage stage, String id, CompletedWork<?, V> stageResult) {
        super(stage, id, stageResult);
    }

    public V result() {
        return getStageResult().getResult().orElse(null);
    }
}
