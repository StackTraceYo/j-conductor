package org.stacktrace.yo.jconductor.core.execution.stage;

import java.util.function.Consumer;

public abstract class StageListener<V> {

    public abstract Consumer<JobStage<V>> onInit();

    public abstract Consumer<JobStage<V>> onStart();

    public abstract Consumer<JobStage<V>> onComplete();

    public abstract Consumer<Throwable> onError();

}
