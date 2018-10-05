package org.stacktrace.yo.remote.orch;

public interface OrchestratorCommunicator {

    OrchestratorRestProtocol.RegisterMessage.Response connect(OrchestratorRestProtocol.RegisterMessage.Request request);

    OrchestratorRestProtocol.UnregisterMessage.Response disconnect(OrchestratorRestProtocol.UnregisterMessage.Request request);

    OrchestratorRestProtocol.NotifyComplete.Response notifyComplete(OrchestratorRestProtocol.NotifyComplete.Request request);

}
