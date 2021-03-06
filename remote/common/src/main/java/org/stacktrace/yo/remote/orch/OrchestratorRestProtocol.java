package org.stacktrace.yo.remote.orch;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrchestratorRestProtocol {

    private static final String ORCH_BASE_PATH = "/orchestrator";

    public enum Route {

        REGISTER("/register"),
        UNREGISTER("/unregister"),
        NOTIFY("/job/complete"),
        JOBS("/jobs"),
        DONE("/jobs/done"),
        PENDING("/jobs/pending"),
        JOB_DETAIL("/jobs/:job_id"),
        JOB_RESULT("/jobs/:job_id/result"),
        HEALTH("/health"),
        STATUS("/config");

        private String path;

        Route(String s) {
            this.path = s;
        }

        public String getPath() {
            return path;
        }

        public URI getRoute(String remoteAddress) throws URISyntaxException {
            return new URI(remoteAddress + ORCH_BASE_PATH + path);
        }
    }

    public static final class JobResultMessage {
        public static final class Response {
            private String id;
            private String worker;
            private Object result;

            public Response() {
            }

            public String getId() {
                return id;
            }

            public Response setId(String id) {
                this.id = id;
                return this;
            }

            public String getWorker() {
                return worker;
            }

            public Response setWorker(String worker) {
                this.worker = worker;
                return this;
            }

            public Object getResult() {
                return result;
            }

            public Response setResult(Object result) {
                this.result = result;
                return this;
            }
        }
    }

    public static final class JobStatusMessage {

        public static final class MultiJobResponse {
            private List<JobStatusMessage.Response> pending = new ArrayList<>();
            private List<JobStatusMessage.Response> done = new ArrayList<>();

            public MultiJobResponse() {
            }

            public MultiJobResponse addPending(JobStatusMessage.Response response) {
                pending.add(response);
                return this;
            }

            public MultiJobResponse addDone(JobStatusMessage.Response response) {
                done.add(response);
                return this;
            }

            public List<Response> getPending() {
                return pending;
            }

            public MultiJobResponse setPending(List<Response> pending) {
                this.pending = pending;
                return this;
            }

            public List<Response> getDone() {
                return done;
            }

            public MultiJobResponse setDone(List<Response> done) {
                this.done = done;
                return this;
            }
        }

        public static final class Response {
            private String id;
            private String status;
            private String worker;

            public Response() {
            }

            public String getId() {
                return id;
            }

            public Response setId(String id) {
                this.id = id;
                return this;
            }

            public String getStatus() {
                return status;
            }

            public Response setStatus(String status) {
                this.status = status;
                return this;
            }

            public String getWorker() {
                return worker;
            }

            public Response setWorker(String worker) {
                this.worker = worker;
                return this;
            }
        }
    }

    public static final class RegisterMessage {

        public static final class Request {
            private List<String> jobs = Collections.emptyList();
            private String address;


            public List<String> getJobs() {
                return jobs;
            }

            public Request addJob(String job) {
                this.jobs.add(job);
                return this;
            }

            public Request setJobs(List<String> jobs) {
                this.jobs = jobs;
                return this;
            }

            public String getAddress() {
                return address;
            }

            public Request setAddress(String address) {
                this.address = address;
                return this;
            }
        }

        public static final class Response {

            private String id;
            private String message;
            private boolean success;

            public String getId() {
                return id;
            }

            public Response setId(String id) {
                this.id = id;
                return this;
            }

            public String getMessage() {
                return message;
            }

            public Response setMessage(String message) {
                this.message = message;
                return this;
            }

            public boolean isSuccess() {
                return success;
            }

            public Response setSuccess(boolean success) {
                this.success = success;
                return this;
            }
        }
    }

    public static final class UnregisterMessage {

        public static final class Request {
            private String address;
            private String id;

            public String getAddress() {
                return address;
            }

            public Request setAddress(String address) {
                this.address = address;
                return this;
            }

            public String getId() {
                return id;
            }

            public Request setId(String id) {
                this.id = id;
                return this;
            }
        }

        public static final class Response {

            private String message;
            private boolean success;

            public String getMessage() {
                return message;
            }

            public Response setMessage(String message) {
                this.message = message;
                return this;
            }

            public boolean isSuccess() {
                return success;
            }

            public Response setSuccess(boolean success) {
                this.success = success;
                return this;
            }
        }
    }

    public static final class NotifyComplete {

        public static final class Request {

            private String jobId;
            private String workerId;
            private Object result;
            private String error;

            public String getJobId() {
                return jobId;
            }

            public Request setJobId(String jobId) {
                this.jobId = jobId;
                return this;
            }

            public String getWorkerId() {
                return workerId;
            }

            public Request setWorkerId(String workerId) {
                this.workerId = workerId;
                return this;
            }

            public Object getResult() {
                return result;
            }

            public Request setResult(Object result) {
                this.result = result;
                return this;
            }

            public String getError() {
                return error;
            }

            public Request setError(String error) {
                this.error = error;
                return this;
            }
        }

        public static final class Response {
            private String message;
            private String jobId;
            private Boolean success;

            public String getMessage() {
                return message;
            }

            public Response setMessage(String message) {
                this.message = message;
                return this;
            }

            public String getJobId() {
                return jobId;
            }

            public Response setJobId(String jobId) {
                this.jobId = jobId;
                return this;
            }

            public Boolean getSuccess() {
                return success;
            }

            public Response setSuccess(Boolean success) {
                this.success = success;
                return this;
            }
        }
    }

}
