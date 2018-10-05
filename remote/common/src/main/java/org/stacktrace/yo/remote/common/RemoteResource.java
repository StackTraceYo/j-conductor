package org.stacktrace.yo.remote.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stacktrace.yo.remote.orch.OrchestratorRestProtocol;

import java.io.IOException;
import java.net.URI;

public abstract class RemoteResource {

    private final ObjectMapper myObjectMapper;
    private static final Logger myLogger = LoggerFactory.getLogger(RemoteResource.class);

    protected RemoteResource() {
        this.myObjectMapper = new ObjectMapper();
    }

    public enum ResourceType {
        ORCH,
        WORKER
    }

    public abstract String remoteAddress();

    public abstract ResourceType resourceType();

    protected <T> T post(OrchestratorRestProtocol.Route route, Object request, Class<T> responseType) {

        try {
            String rq = myObjectMapper.writeValueAsString(request);

            URI postRoute = route.getRoute(remoteAddress());
            HttpPost httpPost = new HttpPost(postRoute);
            httpPost.setEntity(
                    EntityBuilder
                            .create()
                            .setContentType(ContentType.APPLICATION_JSON)
                            .setText(rq)
                            .build()
            );
            return executePost(httpPost, new JsonResponseHandler(), responseType);
        } catch (Exception e) {
            myLogger.debug("Exception calling {}", route, e);
            return null;
        }
    }

    private <T> T executePost(HttpPost post, ResponseHandler<String> handler, Class<T> klass) {
        try (CloseableHttpClient httpClient = httpClient()) {
            myLogger.debug("{} {}", post.getMethod(), post.getURI());
            String responseBody = httpClient.execute(post, handler);
            myLogger.debug("Response Body: {}", responseBody);
            return myObjectMapper.readValue(responseBody, klass);
        } catch (Exception e) {
            myLogger.debug("Exception calling {}", post.getURI(), e);
            return null;
        }
    }

    private CloseableHttpClient httpClient() {
        return HttpClients.createDefault();
    }

    public static class JsonResponseHandler implements ResponseHandler<String> {

        @Override
        public String handleResponse(HttpResponse httpResponse) throws IOException {
            int status = httpResponse.getStatusLine().getStatusCode();
            myLogger.debug("HTTP status code from Request = {}", status);
            if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
                HttpEntity entity = httpResponse.getEntity();
                return entity == null ? null : EntityUtils.toString(entity);
            } else {
                throw new RuntimeException(
                        "Unexpected status from response - " + status
                );
            }
        }
    }

}
