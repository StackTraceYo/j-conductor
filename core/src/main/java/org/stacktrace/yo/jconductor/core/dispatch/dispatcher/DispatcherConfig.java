package org.stacktrace.yo.jconductor.core.dispatch.dispatcher;

public class DispatcherConfig {

    private final boolean runAsync;
    private final int maxConcurrent;

    public DispatcherConfig(boolean runAsync, int maxConcurrent) {
        this.runAsync = runAsync;
        this.maxConcurrent = maxConcurrent;
    }

    public boolean isRunAsync() {
        return runAsync;
    }

    public int getMaxConcurrent() {
        return maxConcurrent;
    }
}
