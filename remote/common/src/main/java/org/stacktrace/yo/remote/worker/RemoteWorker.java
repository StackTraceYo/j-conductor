package org.stacktrace.yo.remote.worker;

import org.stacktrace.yo.remote.common.RemoteResource;

public class RemoteWorker extends RemoteResource implements WorkerCommunicator {

    private final String myLocation;
    private final String myId;

    public RemoteWorker(String myLocation, String myId) {
        this.myLocation = myLocation;
        this.myId = myId;
    }

    @Override
    public String remoteAddress() {
        return myLocation;
    }

    @Override
    public ResourceType resourceType() {
        return ResourceType.WORKER;
    }

    public String getRemoteId() {
        return myId;
    }
}
