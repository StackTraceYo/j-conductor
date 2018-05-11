package org.stacktrace.yo.jconductor.core.job.stage;

import java.util.function.Consumer;

public interface StageListener<V> {

    Consumer<? super JobStage<V>> onComplete();

    //TODO error stage
    Consumer<? super Throwable> onError();

}
