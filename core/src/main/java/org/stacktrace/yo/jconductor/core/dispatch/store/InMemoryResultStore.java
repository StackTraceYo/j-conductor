package org.stacktrace.yo.jconductor.core.dispatch.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stacktrace.yo.jconductor.core.dispatch.work.CompletedWork;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryResultStore implements ResultStore {
    private static final Logger myLogger = LoggerFactory.getLogger(InMemoryResultStore.class.getSimpleName());

    private final Map<String, CompletedWork> myCompletedWork;

    public InMemoryResultStore() {
        this.myCompletedWork = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<CompletedWork> getResult(String id) {
        return Optional.ofNullable(myCompletedWork.get(id));
    }

    @Override
    public boolean putResult(String id, CompletedWork completed) {
        try {
            myCompletedWork.put(id, completed);
            myLogger.debug("Set Result for {}", id);
            return true;
        } catch (Exception e) {
            myLogger.error("Failed to Put Result for {}", id, e);
            return false;
        }
    }
}
