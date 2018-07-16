package org.stacktrace.yo.remote.orch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.stacktrace.yo.remote.orch.core.Orchestrator;
import spark.Response;

import static org.stacktrace.yo.remote.orch.OrchestratorRestProtocol.JobResultMessage;
import static org.stacktrace.yo.remote.orch.OrchestratorRestProtocol.JobStatusMessage;
import static org.stacktrace.yo.remote.orch.OrchestratorRestProtocol.NotifyComplete;
import static org.stacktrace.yo.remote.orch.OrchestratorRestProtocol.RegisterMessage;
import static org.stacktrace.yo.remote.orch.OrchestratorRestProtocol.Route.DONE;
import static org.stacktrace.yo.remote.orch.OrchestratorRestProtocol.Route.JOBS;
import static org.stacktrace.yo.remote.orch.OrchestratorRestProtocol.Route.JOB_DETAIL;
import static org.stacktrace.yo.remote.orch.OrchestratorRestProtocol.Route.JOB_RESULT;
import static org.stacktrace.yo.remote.orch.OrchestratorRestProtocol.Route.NOTIFY;
import static org.stacktrace.yo.remote.orch.OrchestratorRestProtocol.Route.PENDING;
import static org.stacktrace.yo.remote.orch.OrchestratorRestProtocol.Route.REGISTER;
import static org.stacktrace.yo.remote.orch.OrchestratorRestProtocol.Route.UNREGISTER;
import static org.stacktrace.yo.remote.orch.OrchestratorRestProtocol.UnregisterMessage;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

public class OrchestratorRunner {
    private static ObjectMapper myMapper = new ObjectMapper();
    private static final Orchestrator myOrchestrator = new Orchestrator();

    public static void main(String args[]) {
        port(8888);

        post(REGISTER.getPath(), (request, response) -> {
            RegisterMessage.Request reg = myMapper.readValue(request.bodyAsBytes(),
                    RegisterMessage.Request.class);
            return setResponseAndWrite(response, null);
        });

        post(UNREGISTER.getPath(), (request, response) -> {
            UnregisterMessage.Request unreg = myMapper.readValue(request.bodyAsBytes(),
                    UnregisterMessage.Request.class);
            return setResponseAndWrite(response, null);
        });

        post(NOTIFY.getPath(), (request, response) -> {
            NotifyComplete.Request notify = myMapper.readValue(request.bodyAsBytes(),
                    NotifyComplete.Request.class);
            return setResponseAndWrite(response, null);
        });

        get(JOBS.getPath(), (request, response) -> {
            JobStatusMessage.MultiJobResponse all = myMapper.readValue(request.bodyAsBytes(),
                    JobStatusMessage.MultiJobResponse.class);
            return setResponseAndWrite(response, null);
        });

        get(JOB_DETAIL.getPath(), (request, response) -> {
            JobStatusMessage.Response res = myMapper.readValue(request.bodyAsBytes(),
                    JobStatusMessage.Response.class);
            return setResponseAndWrite(response, null);
        });

        get(JOB_RESULT.getPath(), (request, response) -> {
            JobResultMessage.Response res = myMapper.readValue(request.bodyAsBytes(),
                    JobResultMessage.Response.class);
            return setResponseAndWrite(response, null);
        });

        get(PENDING.getPath(), (request, response) -> {
            JobStatusMessage.MultiJobResponse pending = myMapper.readValue(request.bodyAsBytes(),
                    JobStatusMessage.MultiJobResponse.class);
            return setResponseAndWrite(response, null);
        });

        get(DONE.getPath(), (request, response) -> {
            JobStatusMessage.MultiJobResponse pending = myMapper.readValue(request.bodyAsBytes(),
                    JobStatusMessage.MultiJobResponse.class);
            return setResponseAndWrite(response, null);
        });
    }

    private static Object setResponseAndWrite(Response response, Object entity) throws JsonProcessingException {
        response.type("application/json");
        return myMapper.writeValueAsString(entity);
    }
}



