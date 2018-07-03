package org.stacktrace.yo.jconductor.core.schedule;

import org.stacktrace.yo.jconductor.core.dispatch.flow.JobFlow;
import org.stacktrace.yo.jconductor.core.dispatch.schedule.JobPlan;
import org.stacktrace.yo.jconductor.core.execution.work.Job;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class TestJobPlan extends JobPlan<TestJobPlan.BasicTestJob, String, String> {

    public TestJobPlan() {
        super("TestJobPlan");
    }

    static class BasicTestJob implements Job<String, String> {
        @Override
        public String doWork(String params) {
            return "Return " + params;
        }

        @Override
        public void postRun() {
            System.out.println("Cleanup");
        }

        @Override
        public void init(String params) {
            System.out.println("Params: " + params);
        }
    }

    @Override
    public int initialDelay() {
        return 0;
    }

    @Override
    public int period() {
        return 10;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }

    @Override
    public Supplier<BasicTestJob> job() {
        return BasicTestJob::new;
    }

    @Override
    public Supplier<String> jobParams() {
        return () -> "Supplied Params";
    }

    public static void main(String[] args) {
        new JobFlow()
                .addPlan(new TestJobPlan())
                .thenRun(new TestJobPlan())
                .andThenRun(
                        new JobFlow.JobPlanFlow(new TestJobPlan())
                                .thenRun(new TestJobPlan())
                                .thenRun(new TestJobPlan())
                                .andThenRun(
                                        new JobFlow.JobPlanFlow(new TestJobPlan())
                                )
                );
    }
}
