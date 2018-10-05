package org.stacktrace.yo.remote.strategy;

import org.stacktrace.yo.remote.worker.RemoteWorker;

public interface DispatchStrategy {

    RemoteWorker pick();

}
