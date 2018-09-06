package org.stacktrace.yo.jconductor.core.dispatch.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stacktrace.yo.jconductor.core.dispatch.store.InMemoryResultStore;
import org.stacktrace.yo.jconductor.core.dispatch.store.ResultStore;
import org.stacktrace.yo.jconductor.core.dispatch.work.ScheduledWork;
import org.stacktrace.yo.jconductor.core.execution.job.FutureWorker;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListenerBuilder;
import org.stacktrace.yo.jconductor.core.execution.work.Job;
import org.stacktrace.yo.jconductor.core.util.supplier.MultiSupplier;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class FutureDispatcher implements AsyncDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(FutureDispatcher.class.getSimpleName());

    private final ResultStore myResultStore;
    private final ExecutorService myExecutorService;
    private final AtomicInteger myRunningCount;
    private final AtomicInteger myPendingCount;

    public FutureDispatcher(int concurrent) {
        myResultStore = new InMemoryResultStore();
        myExecutorService = Executors.newFixedThreadPool(concurrent);
        myRunningCount = new AtomicInteger(0);
        myPendingCount = new AtomicInteger(0);
    }

    public FutureDispatcher() {
        this(1);
    }

    public <T, V> String schedule(Job<T, V> job, Supplier<T> params) {
        String id = UUID.randomUUID().toString();
        ScheduledWork<T, V> scheduledWork = new ScheduledWork<>(job, params, id);
        run(schedule(scheduledWork, id));
        return id;
    }

    @Override
    public <T, V> String schedule(Job<T, V> job, Supplier<T> params, StageListener<V> listener) {
        String id = UUID.randomUUID().toString();
        ScheduledWork<T, V> scheduledWork = new ScheduledWork<>(job, params, id, listener);
        run(schedule(scheduledWork, id));
        return id;
    }

    @Override
    public <T, V> String schedule(Job<T, V> job, MultiSupplier<T> params) {
        return null;
    }

    @Override
    public <T, V> String schedule(Job<T, V> job, MultiSupplier<T> params, StageListener<V> listener) {
        return null;
    }

    @Override
    public <T, V> CompletableFuture<V> scheduleAsync(Job<T, V> job, MultiSupplier<T> params) {
        return null;
    }

    @Override
    public <T, V> CompletableFuture<V> scheduleAsync(Job<T, V> job, MultiSupplier<T> params, StageListener<V> listener) {
        return null;
    }

    @Override
    public <T, V> CompletableFuture<V> scheduleAsync(Job<T, V> job, Supplier<T> params) {
        String id = UUID.randomUUID().toString();
        ScheduledWork<T, V> scheduledWork = new ScheduledWork<>(job, params, id);
        return run(schedule(scheduledWork, id));
    }

    @Override
    public <T, V> CompletableFuture<V> scheduleAsync(Job<T, V> job, Supplier<T> params, StageListener<V> listener) {
        String id = UUID.randomUUID().toString();
        ScheduledWork<T, V> scheduledWork = new ScheduledWork<>(job, params, id, listener);
        return run(schedule(scheduledWork, id));
    }

    private <T, V> CompletableFuture<V> run(FutureWorker<T, V> asynchronousJob) {
        myPendingCount.getAndIncrement();
        return asynchronousJob.run(myExecutorService);
    }

    private <T, V> FutureWorker<T, V> schedule(ScheduledWork<T, V> scheduledWork, String id) {
        LOGGER.debug("[FutureDispatcher] scheduling new job {}", id);
        return createAsyncJob(scheduledWork);
    }

    @SuppressWarnings("unchecked")
    private <T, V> FutureWorker<T, V> createAsyncJob(ScheduledWork<T, V> work) {
        return new FutureWorker<>(work.getId(), work.getJob(), work.getParams(),
                new StageListenerBuilder<V>()
                        .onStart(running -> {
                            LOGGER.debug("[FutureDispatcher] Job Started: {}", work.getId());
                            myPendingCount.getAndDecrement();
                            myRunningCount.getAndIncrement();
                        })
                        .andThen(work.onStart())
                        .next()
                        .onComplete(
                                completed -> {
                                    LOGGER.debug("[FutureDispatcher] Job Completed: {}", work.getId());
                                    myRunningCount.getAndDecrement();
                                })
                        .andThen(work.onComplete())
                        .next()
                        .onError(
                                error -> {
                                    LOGGER.error("[FutureDispatcher] Job Errored: {}", work.getId(), error);
                                    myRunningCount.getAndDecrement();
                                })
                        .andThen(work.onError())
                        .finish()
        );
    }

    public boolean shutdown() {
        LOGGER.debug("[FutureDispatcher] Shutting Down");
        if (this.isRunning()) {
            LOGGER.debug("[FutureDispatcher] Waiting for Work to finish");
            while (this.isRunning()) {
            }
        }
        LOGGER.debug("[FutureDispatcher] Pending: {} ", myPendingCount.toString());
        LOGGER.debug("[FutureDispatcher] Running: {} ", myRunningCount.toString());
        LOGGER.debug("[FutureDispatcher] Shutting Down");
        myExecutorService.shutdown();
        while (this.isActive()) {
        }
        LOGGER.debug("[FutureDispatcher] Shut Down Gracefully");
        return true;
    }

    public boolean isRunning() {
        return myPendingCount.get() > 0 || myRunningCount.get() > 0;
    }

    public boolean isActive() {
        return !myExecutorService.isShutdown();
    }
}
