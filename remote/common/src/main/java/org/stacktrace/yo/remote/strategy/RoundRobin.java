package org.stacktrace.yo.remote.strategy;

import org.stacktrace.yo.remote.worker.RemoteWorker;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobin extends AbstractDispatchStrategy {

    private final AtomicInteger myCurrentWorker = new AtomicInteger(0);

    protected RoundRobin(List<RemoteWorker> myWorkers) {
        super(myWorkers);
    }

    @Override
    public RemoteWorker pick() {
        Integer picked = myCurrentWorker.getAndIncrement() % myWorkers.size();
        return myWorkers.get(picked);
    }
}
