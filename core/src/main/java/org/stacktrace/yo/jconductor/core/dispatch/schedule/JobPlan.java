package org.stacktrace.yo.jconductor.core.dispatch.schedule;

import org.stacktrace.yo.jconductor.core.execution.work.Job;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * The type Job plan.
 * <p>
 * This class represents the blueprint for executing some unit of work
 */
public abstract class JobPlan<T extends Job<V, ?>, V> {

    public final String myPlanName;

    protected JobPlan(String myPlanName) {
        this.myPlanName = myPlanName;
    }

    /**
     * Initial delay.
     * <p>
     * After the plan is submitting how much time should pass before executing
     *
     * @return the int
     */
    public abstract int initialDelay();

    /**
     * Period.
     * <p>
     * The time period that should pass before running again
     *
     * @return the int
     */
    public abstract int period();

    /**
     * Prioity.
     * <p>
     * <p>
     * If there are multiple jobs running, the scheduler will pick the plan with highest priority
     *
     * @return the int
     */
    public abstract Integer priority();

    /**
     * Time unit in which the initial delay and the periodic executions are performed
     *
     * @return the time unit
     */
    public abstract TimeUnit timeUnit();

    /**
     * A supplier for the {@link Job}. that this plan supports
     *
     * @return the supplier
     */
    public abstract Supplier<T> supplyWork();

    /**
     * A paramaterized supplier for the {@link Job}. that this plan supports
     * <p>
     * Optional
     *
     * @return the supplier
     */
    public abstract Supplier<V> supplyParams();

}
