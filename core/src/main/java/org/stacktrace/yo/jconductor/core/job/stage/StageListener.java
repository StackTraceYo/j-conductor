package org.stacktrace.yo.jconductor.core.job.stage;

import java.util.function.Consumer;

public interface StageListener<V> {

    Consumer<JobStage<V>> onInit();

    Consumer<JobStage<V>> onStart();

    Consumer<JobStage<V>> onComplete();

    //TODO error stage
    Consumer<Throwable> onError();

}
