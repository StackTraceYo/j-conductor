package org.stacktrace.yo.remote.orch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.stacktrace.yo.remote.orch.core.Orchestrator;

import java.util.ArrayList;

import static org.stacktrace.yo.remote.orch.OrchestratorRestProtocol.Route.*;
import static spark.Spark.port;
import static spark.Spark.post;

public class OrchestratorRunner {
    private static ObjectMapper myMapper = new ObjectMapper();
    private static final Orchestrator myOrchestrator = new Orchestrator();

    public static void main(String args[]) {
        port(8888);

        post(REGISTER.getPath(), (request, response) -> {
            request.queryParams("name");
            response.type("application/json");
            return myMapper.writeValueAsString(new ArrayList<>());
        });

        post(UNREGISTER.getPath(), (request, response) -> {
            request.queryParams("name");
            response.type("application/json");
            return myMapper.writeValueAsString(new ArrayList<>());
        });

        post(NOTIFY.getPath(), (request, response) -> {
            request.queryParams("name");
            response.type("application/json");
            return myMapper.writeValueAsString(new ArrayList<>());
        });
    }
}



