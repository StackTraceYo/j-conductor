package org.stacktrace.yo.jconductor.core.execution.stage;

import java.util.function.Consumer;

public interface StageListener<V> {

    Consumer<JobStage<V>> onStart();

    Consumer<CompletedJobStage<V>> onComplete();

    Consumer<Throwable> onError();


    class DefaultStageListener<V> implements StageListener<V> {

        private final Consumer<JobStage<V>> onStart;
        private final Consumer<CompletedJobStage<V>> onComplete;
        private final Consumer<Throwable> onError;

        public DefaultStageListener(Consumer<JobStage<V>> onStart, Consumer<CompletedJobStage<V>> onComplete, Consumer<Throwable> onError) {
            this.onStart = onStart;
            this.onComplete = onComplete;
            this.onError = onError;
        }

        @Override
        public Consumer<JobStage<V>> onStart() {
            return onStart;
        }

        @Override
        public Consumer<CompletedJobStage<V>> onComplete() {
            return onComplete;
        }

        @Override
        public Consumer<Throwable> onError() {
            return onError;
        }
    }

    class NoOpListener<V> implements StageListener<V> {


        @Override
        public Consumer<JobStage<V>> onStart() {
            return vJobStage -> {
            };
        }

        @Override
        public Consumer<CompletedJobStage<V>> onComplete() {
            return vJobStage -> {
            };
        }

        @Override
        public Consumer<Throwable> onError() {
            return vJobStage -> {
            };
        }
    }

}
