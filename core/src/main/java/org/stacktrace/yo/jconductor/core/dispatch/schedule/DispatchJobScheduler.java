package org.stacktrace.yo.jconductor.core.dispatch.schedule;

import org.stacktrace.yo.jconductor.core.dispatch.dispatcher.SchedulingDispatcher;
import org.stacktrace.yo.jconductor.core.dispatch.work.CompletedWork;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListenerBuilder;
import org.stacktrace.yo.jconductor.core.execution.work.Job;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class DispatchJobScheduler {

    private final Map<String, JobPlan<?, ?, ?>> myJobPlans;
    private final SchedulingDispatcher myDispatcher;
    private final Set<String> myScheduledJobs;

    public DispatchJobScheduler(SchedulingDispatcher dispatcher) {
        this(dispatcher, new HashMap<>());
    }

    public DispatchJobScheduler(SchedulingDispatcher dispatcher, Map<String, JobPlan<?, ?, ?>> jobPlans) {
        myJobPlans = jobPlans;
        myDispatcher = dispatcher;
        myScheduledJobs = new ConcurrentSkipListSet<>();
    }

    public DispatchJobScheduler addPlan(String planName, JobPlan plan) {
        this.myJobPlans.put(planName, plan);
        return this;
    }

    public DispatchJobScheduler addPlan(JobPlan<?, ?, ?> plan) {
        return addPlan(plan.myPlanName, plan);
    }

    public void start() {
        myJobPlans.forEach((s, jobPlan) -> {
            Runnable runnable = createRunnable(jobPlan);
            myDispatcher.scheduler().scheduleAtFixedRate(runnable, jobPlan.initialDelay(), jobPlan.period(), jobPlan.timeUnit());
        });
    }

    private <JobType extends Job<Param, Result>, Param, Result> Runnable createRunnable(JobPlan<JobType, Param, Result> plan) {
        return () -> {
            String jobId = myDispatcher.schedule(plan.job().get(), plan.jobParams().get(), listenerForPlan(plan));
            plan.setExecutionId(jobId);
            myScheduledJobs.add(jobId);
            myJobPlans.put(jobId, plan);
        };
    }

    private <JobType extends Job<Param, Result>, Param, Result> StageListener<Result> listenerForPlan(JobPlan<JobType, Param, Result> plan) {
        return new StageListenerBuilder<Result>()
                .onComplete(completed -> {
                    String jobId = completed.getId();
                    myScheduledJobs.remove(jobId);
                    myJobPlans.remove(jobId);
                    myDispatcher.getResultStore().putResult(jobId,
                            new CompletedWork<>(
                                    completed.getStageResult(),
                                    plan.jobParams().get(),
                                    plan.job().getClass().toGenericString(),
                                    completed.getId()
                            ));
                })
                .onError(error -> {
                    String jobId = plan.getExecutionId();
                    myScheduledJobs.remove(jobId);
                    myJobPlans.remove(jobId);
                    myDispatcher.getResultStore().putResult(plan.getPlanName(),
                            new CompletedWork<>(
                                    error,
                                    plan.jobParams().get(),
                                    plan.job().getClass().toGenericString(),
                                    plan.getPlanName()
                            )
                    );
                })
                .build();
    }
}
