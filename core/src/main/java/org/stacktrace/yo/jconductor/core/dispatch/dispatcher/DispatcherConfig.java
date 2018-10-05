package org.stacktrace.yo.jconductor.core.dispatch.dispatcher;

public class DispatcherConfig {

    private int maxConcurrent;
    private boolean scheduleEnabled;

    public DispatcherConfig() {
    }

    public int getMaxConcurrent() {
        return maxConcurrent;
    }

    public DispatcherConfig setMaxConcurrent(int maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
        return this;
    }

    public boolean isScheduleEnabled() {
        return scheduleEnabled;
    }

    public DispatcherConfig setScheduleEnabled(boolean scheduleEnabled) {
        this.scheduleEnabled = scheduleEnabled;
        return this;
    }
}
