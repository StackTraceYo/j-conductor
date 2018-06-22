package org.stacktrace.yo.jconductor.core.dispatch.schedule;

import org.stacktrace.yo.jconductor.core.dispatch.dispatcher.Dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class DispatchJobScheduler {

    private final ExecutorService myExecutorService;
    private final Map<String, JobPlan> myJobPlans;
    private final Dispatcher myDispatcher;

    public DispatchJobScheduler(Dispatcher dispatcher, ExecutorService executorService) {
        this(dispatcher, executorService, new HashMap<>());
    }

    public DispatchJobScheduler(Dispatcher dispatcher, ExecutorService executorService, Map<String, JobPlan> jobPlans) {
        myExecutorService = executorService;
        myJobPlans = jobPlans;
        myDispatcher = dispatcher;
    }

    public DispatchJobScheduler addPlan(String planName, JobPlan plan) {
        this.myJobPlans.put(planName, plan);
        return this;
    }

    public DispatchJobScheduler addPlan(JobPlan plan) {
        return addPlan(plan.myPlanName, plan);
    }

    public void init() {

    }
}
