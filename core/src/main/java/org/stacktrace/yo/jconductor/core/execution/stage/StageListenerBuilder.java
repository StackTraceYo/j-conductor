package org.stacktrace.yo.jconductor.core.execution.stage;

import java.util.function.Consumer;

public final class StageListenerBuilder<V> {

    private Consumer<JobStage<V>> myStartConsumer;
    private Consumer<CompletedJobStage<V>> myCompleteConsumer;
    private Consumer<Throwable> myErrorConsumer;

    public Starter<V> onStart(Consumer<JobStage<V>> onStart) {
        return new Starter<V>(this, onStart);
    }

    public Completer<V> onComplete(Consumer<CompletedJobStage<V>> onComplete) {
        return new Completer<V>(this, onComplete);
    }

    public Errorer<V> onError(Consumer<Throwable> onError) {
        return new Errorer<V>(this, onError);
    }

    public StageListener<V> build() {
        return new StageListener.DefaultStageListener<>(defaultNull(myStartConsumer), defaultNull(myCompleteConsumer), defaultNull(myErrorConsumer));
    }

    private static <V> Consumer<V> defaultNull(Consumer<V> consumer) {
        return consumer == null ? v -> {
        } : consumer;
    }

    public static class Completer<V> {

        private Consumer<CompletedJobStage<V>> onComplete;
        private StageListenerBuilder<V> builder;

        private Completer(StageListenerBuilder<V> builder, Consumer<CompletedJobStage<V>> onComplete) {
            this.builder = builder;
            this.onComplete = onComplete;
        }

        public Completer<V> andThen(Consumer<CompletedJobStage<V>> onComplete) {
            this.onComplete = this.onComplete.andThen(onComplete);
            return this;
        }

        public StageListenerBuilder<V> next() {
            builder.myCompleteConsumer = this.onComplete;
            return builder;
        }

        public StageListener<V> finish() {
            return next().build();
        }
    }

    public static class Starter<V> {

        private Consumer<JobStage<V>> onStart;
        private StageListenerBuilder<V> builder;

        private Starter(StageListenerBuilder<V> builder, Consumer<JobStage<V>> onStart) {
            this.builder = builder;
            this.onStart = onStart;
        }

        public Starter<V> andThen(Consumer<JobStage<V>> onStart) {
            this.onStart = this.onStart.andThen(onStart);
            return this;
        }

        public StageListenerBuilder<V> next() {
            builder.myStartConsumer = this.onStart;
            return builder;
        }

        public StageListener<V> finish() {
            return next().build();
        }
    }

    public static class Errorer<V> {

        private Consumer<Throwable> onError;
        private StageListenerBuilder<V> builder;

        private Errorer(StageListenerBuilder<V> builder, Consumer<Throwable> onError) {
            this.builder = builder;
            this.onError = onError;
        }

        public Errorer<V> andThen(Consumer<Throwable> onError) {
            this.onError = this.onError.andThen(onError);
            return this;
        }

        public StageListenerBuilder<V> next() {
            builder.myErrorConsumer = this.onError;
            return builder;
        }

        public StageListener<V> finish() {
            return next().build();
        }
    }

}