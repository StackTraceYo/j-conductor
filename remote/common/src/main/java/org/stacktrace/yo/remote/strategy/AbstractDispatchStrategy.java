package org.stacktrace.yo.remote.strategy;

import org.stacktrace.yo.remote.worker.RemoteWorker;

import java.util.List;

public abstract class AbstractDispatchStrategy implements DispatchStrategy {

    protected final List<RemoteWorker> myWorkers;

    protected AbstractDispatchStrategy(List<RemoteWorker> myWorkers) {
        this.myWorkers = myWorkers;
    }

    public List<RemoteWorker> getWorkers() {
        return myWorkers;
    }
}
