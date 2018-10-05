package org.stacktrace.yo.remote.orch.core;

public interface IOrchestrator {

    void detail();

    void all();

    void pending();

    void done();

    void jobs();

    void status();

    void notifyJob();

    void register();

    void unregister();
}
