package org.stacktrace.yo.jconductor.core.job;

public abstract class AbstractJob<T, V> implements Job<T, V> {

    protected final T params;
    protected V result;
    protected String id;
    protected boolean running;
    protected boolean completed;
    protected boolean started;

    public AbstractJob(T params) {
        this.params = params;
        this.running = false;
        this.completed = false;
    }

    public V start(String id) {
        this.id = id;
        this.running = true;
        this.result = this.execute(this.params);
        this.completed = true;
        return this.result;
    }

    public V getResult() {
        if (this.started) {
            // todo throw started
            return null;
        } else if (this.completed) {
            return this.result;
        } else if (this.running) {
            // todo throw running
            return null;
        } else {
            // todo throw exception
            return null;
        }
    }

    public T getParams() {
        return params;
    }

    public String getId() {
        return id;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isStarted() {
        return started;
    }
}
