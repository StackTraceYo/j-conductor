package org.stacktrace.yo.jconductor.core.execution.stage;

import java.util.function.Consumer;

public interface StageListener<V> {

    Consumer<JobStage<V>> onInit();

    Consumer<JobStage<V>> onStart();

    Consumer<JobStage<V>> onComplete();

    Consumer<Throwable> onError();

    class StageListenerBuilder<V> {

        private Consumer<JobStage<V>> onInit;
        private Consumer<JobStage<V>> onStart;
        private Consumer<JobStage<V>> onComplete;
        private Consumer<Throwable> onError;


        public StageListenerBuilder<V> onInit(Consumer<JobStage<V>> onInit) {
            if (this.onInit != null && onInit != null) {
                this.onInit = this.onInit.andThen(onInit);
            } else if(onInit != null) {
                this.onInit = onInit;
            }
            return this;
        }

        public StageListenerBuilder<V> onStart(Consumer<JobStage<V>> onStart) {
            if (this.onStart != null && onStart != null) {
                this.onStart = this.onStart.andThen(onStart);
            } else if (onStart != null) {
                this.onStart = onStart;
            }
            return this;
        }

        public StageListenerBuilder<V> onComplete(Consumer<JobStage<V>> onComplete) {
            if (this.onComplete != null && onComplete != null) {
                this.onComplete = this.onComplete.andThen(onComplete);
            } else if(onComplete != null) {
                this.onComplete = onComplete;
            }
            return this;
        }

        public StageListenerBuilder<V> onError(Consumer<Throwable> onError) {
            if (this.onError != null && onError != null) {
                this.onError = this.onError.andThen(onError);
            } else if(onError != null) {
                this.onError = onError;
            }
            return this;
        }

        public StageListenerBuilder<V> bindListener(StageListener<V> listener) {
            if (listener != null) {
                onInit(listener.onInit());
                onStart(listener.onStart());
                onComplete(listener.onComplete());
                onError(listener.onError());
            }
            return this;
        }

        public StageListener<V> build() {

            return new StageListener<V>() {
                @Override
                public Consumer<JobStage<V>> onInit() {
                    return onInit;
                }

                @Override
                public Consumer<JobStage<V>> onStart() {
                    return onStart;
                }

                @Override
                public Consumer<JobStage<V>> onComplete() {
                    return onComplete;
                }

                @Override
                public Consumer<Throwable> onError() {
                    return onError;
                }
            };
        }


    }

}
