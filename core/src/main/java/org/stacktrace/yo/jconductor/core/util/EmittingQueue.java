package org.stacktrace.yo.jconductor.core.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class EmittingQueue<T> extends LinkedBlockingQueue<T> {

    private final Consumer<Event<T>> onQueued;
    private final Consumer<Event<T>> onDeque;

    public EmittingQueue(QueueEventListener listener) {
        this.onQueued = listener.onQueued();
        this.onDeque = listener.onDeque();
    }

    public EmittingQueue(Consumer<Event<T>> onQueued) {
        this.onQueued = onQueued;
        this.onDeque = null;
    }

    public EmittingQueue(Consumer<Event<T>> onQueued, Consumer<Event<T>> onDeque) {
        this.onQueued = onQueued;
        this.onDeque = onDeque;
    }

    @Override
    public boolean offer(T t) {
        boolean success = super.offer(t);
        if (success) {
            emitQueue(t);
        }
        return success;
    }

    @Override
    public T poll() {
        T queueItem = super.poll();
        if (queueItem != null) {
            emitDequeue(queueItem);
        }
        return queueItem;
    }

    private void emitQueue(T item) {
        if (this.onQueued != null) {
            this.onQueued.accept(QueueEvent.QUEUED.createEvent(item));
        }
    }

    private void emitDequeue(T item) {
        if (this.onDeque != null) {
            this.onDeque.accept(QueueEvent.DEQUEUED.createEvent(item));
        }
    }

    public enum QueueEvent {
        QUEUED,
        DEQUEUED;

        public <T> Event<T> createEvent(T item) {
            return new Event<>(this, item);
        }
    }

    public interface QueueEventListener<T> {
        Consumer<Event<T>> onQueued();

        Consumer<Event<T>> onDeque();
    }

    public static class Event<T> {

        private final QueueEvent e;
        private final T item;

        public Event(QueueEvent e, T item) {
            this.e = e;
            this.item = item;
        }

        public QueueEvent getEvent() {
            return e;
        }

        public T getItem() {
            return item;
        }
    }

}
