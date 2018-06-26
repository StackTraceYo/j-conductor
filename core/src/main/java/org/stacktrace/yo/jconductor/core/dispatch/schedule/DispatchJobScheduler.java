package org.stacktrace.yo.jconductor.core.dispatch.schedule;

import org.stacktrace.yo.jconductor.core.dispatch.dispatcher.SchedulingDispatcher;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListenerBuilder;
import org.stacktrace.yo.jconductor.core.execution.work.Job;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledFuture;

public class DispatchJobScheduler {

    private final Map<String, JobPlan<?, ?, ?>> myJobPlans;
    private final Set<String> myScheduledJobs;
    private final Map<String, ScheduledFuture> myScheduledPlans;
    private final SchedulingDispatcher myDispatcher;

    public DispatchJobScheduler(SchedulingDispatcher dispatcher) {
        this(dispatcher, new HashMap<>());
    }

    public DispatchJobScheduler(SchedulingDispatcher dispatcher, Map<String, JobPlan<?, ?, ?>> jobPlans) {
        myJobPlans = jobPlans;
        myDispatcher = dispatcher;
        myScheduledJobs = new ConcurrentSkipListSet<>();
        myScheduledPlans = new ConcurrentHashMap<>();
    }

    public DispatchJobScheduler addPlan(String planName, JobPlan plan) {
        this.myJobPlans.put(planName, plan);
        return this;
    }

    public DispatchJobScheduler addPlan(JobPlan<?, ?, ?> plan) {
        return addPlan(plan.myPlanName, plan);
    }

    public DispatchJobScheduler addAndSchedulePlan(JobPlan<?, ?, ?> plan) {
        addPlan(plan.myPlanName, plan);
        update();
        return this;
    }

    public DispatchJobScheduler addAndSchedulePlan(String planName, JobPlan<?, ?, ?> plan) {
        addPlan(planName, plan);
        update();
        return this;
    }

    public DispatchJobScheduler update() {
        myJobPlans.forEach((s, jobPlan) -> {
            if (!myScheduledPlans.containsKey(s)) {
                Runnable runnable = createJob(jobPlan);
                ScheduledFuture future = myDispatcher.scheduler().scheduleAtFixedRate(runnable, jobPlan.initialDelay(), jobPlan.period(), jobPlan.timeUnit());
                myScheduledPlans.put(s, future);
            }
        });
        return this;
    }


    public void start() {
        myJobPlans.forEach((s, jobPlan) -> {
            Runnable runnable = createJob(jobPlan);
            ScheduledFuture future = myDispatcher.scheduler().scheduleAtFixedRate(runnable, jobPlan.initialDelay(), jobPlan.period(), jobPlan.timeUnit());
            myScheduledPlans.put(s, future);
        });
    }

    private <JobType extends Job<Param, Result>, Param, Result> Runnable createJob(JobPlan<JobType, Param, Result> plan) {
        return () -> {
            String jobId = myDispatcher.schedule(plan.job().get(), plan.jobParams().get(), createPlanListener(plan));
            plan.setExecutionId(jobId);
            myScheduledJobs.add(jobId);
            myJobPlans.put(jobId, plan);
        };
    }

    private <JobType extends Job<Param, Result>, Param, Result> StageListener<Result> createPlanListener(JobPlan<JobType, Param, Result> plan) {
        return new StageListenerBuilder<Result>()
                .onComplete(completed -> {
                    String jobId = completed.getId();
                    myScheduledJobs.remove(jobId);
                    myJobPlans.remove(jobId);
                })
                .onError(error -> {
                    String jobId = plan.getExecutionId();
                    myScheduledJobs.remove(jobId);
                    myJobPlans.remove(jobId);
                })
                .build();
    }
}
