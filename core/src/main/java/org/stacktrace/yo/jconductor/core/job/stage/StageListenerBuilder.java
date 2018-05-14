package org.stacktrace.yo.jconductor.core.job.stage;

import java.util.function.Consumer;

public class StageListenerBuilder {

    private Consumer<JobStage> onInit;
    private Consumer<JobStage> onStart;
    private Consumer<JobStage> onComplete;
    private Consumer<Throwable> onError;


    public StageListenerBuilder onInit(Consumer<JobStage> onInit) {
        if (this.onInit != null) {
            this.onInit.andThen(onInit);
        } else {
            this.onInit = onInit;
        }
        return this;
    }

    public StageListenerBuilder onStart(Consumer<JobStage> onStart) {
        if (this.onStart != null) {
            this.onStart.andThen(onStart);
        } else {
            this.onStart = onStart;
        }
        return this;
    }

    public StageListenerBuilder onComplete(Consumer<JobStage> onComplete) {
        if (this.onComplete != null) {
            this.onComplete.andThen(onComplete);
        } else {
            this.onComplete = onComplete;
        }
        return this;
    }

    public StageListenerBuilder onError(Consumer<Throwable> onError) {
        if (this.onError != null) {
            this.onError.andThen(onError);
        } else {
            this.onError = onError;
        }
        return this;
    }

    public StageListener build() {

        return new StageListener() {
            @Override
            public Consumer<JobStage> onInit() {
                return onInit;
            }

            @Override
            public Consumer<JobStage> onStart() {
                return onStart;
            }

            @Override
            public Consumer<JobStage> onComplete() {
                return onComplete;
            }

            @Override
            public Consumer<Throwable> onError() {
                return onError;
            }
        };
    }


}
