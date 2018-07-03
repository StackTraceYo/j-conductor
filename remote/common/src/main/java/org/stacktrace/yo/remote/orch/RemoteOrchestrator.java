package org.stacktrace.yo.remote.orch;

import org.stacktrace.yo.remote.common.RemoteResource;

public class RemoteOrchestrator extends RemoteResource implements OrchestratorCommunicator {

    private final String myLocation;
    private final String myId;

    public RemoteOrchestrator(String myLocation, String myId) {
        this.myLocation = myLocation;
        this.myId = myId;
    }

    @Override
    public String remoteAddress() {
        return myLocation;
    }

    @Override
    public ResourceType resourceType() {
        return ResourceType.ORCH;
    }

    public String getRemoteId() {
        return myId;
    }

    @Override
    public OrchestratorRestProtocol.RegisterMessage.Response connect(OrchestratorRestProtocol.RegisterMessage.Request request) {
        return post(OrchestratorRestProtocol.Route.REGISTER, request, OrchestratorRestProtocol.RegisterMessage.Response.class);
    }

    @Override
    public OrchestratorRestProtocol.UnregisterMessage.Response disconnect(OrchestratorRestProtocol.UnregisterMessage.Request request) {
        return post(OrchestratorRestProtocol.Route.UNREGISTER, request, OrchestratorRestProtocol.UnregisterMessage.Response.class);
    }

    @Override
    public OrchestratorRestProtocol.NotifyComplete.Response notifyComplete(OrchestratorRestProtocol.NotifyComplete.Request request) {
        return post(OrchestratorRestProtocol.Route.NOTIFY, request, OrchestratorRestProtocol.NotifyComplete.Response.class);
    }
}
