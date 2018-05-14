package org.stacktrace.yo.jconductor.core.util;

import java.util.PriorityQueue;
import java.util.function.Consumer;

public class EmittingQueue<T> extends PriorityQueue<T> {

    private final Consumer<Event> onQueued;
    private final Consumer<Event> onDeque;

    public EmittingQueue(QueueEventListener listener) {
        this.onQueued = listener.onQueued();
        this.onDeque = listener.onDeque();
    }

    public EmittingQueue(Consumer<Event> onQueued) {
        this.onQueued = onQueued;
        this.onDeque = null;
    }

    public EmittingQueue(Consumer<Event> onQueued, Consumer<Event> onDeque) {
        this.onQueued = onQueued;
        this.onDeque = onDeque;
    }

    @Override
    public boolean offer(T t) {
        boolean success = super.offer(t);
        if (success) {
            emitQueue();
        }
        return success;
    }

    @Override
    public T poll() {
        T queueItem = super.poll();
        if (queueItem != null) {
            emitDequeue();
        }
        return queueItem;
    }

    private void emitQueue() {
        if (this.onQueued != null) {
            this.onQueued.accept(QueueEvent.QUEUED.createEvent());
        }
    }

    private void emitDequeue() {
        if (this.onDeque != null) {
            this.onDeque.accept(QueueEvent.DEQUEUED.createEvent());
        }
    }

    public enum QueueEvent {
        QUEUED,
        DEQUEUED;

        public Event createEvent() {
            return new Event(this);
        }
    }

    public interface QueueEventListener {
        Consumer<Event> onQueued();

        Consumer<Event> onDeque();
    }

    public static class Event {

        private final QueueEvent e;

        public Event(QueueEvent e) {
            this.e = e;
        }

        public QueueEvent getEvent() {
            return e;
        }
    }

}
