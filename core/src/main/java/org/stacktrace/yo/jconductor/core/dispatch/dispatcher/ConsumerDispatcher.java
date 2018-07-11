package org.stacktrace.yo.jconductor.core.dispatch.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stacktrace.yo.jconductor.core.dispatch.schedule.SchedulingDispatcher;
import org.stacktrace.yo.jconductor.core.dispatch.store.InMemoryResultStore;
import org.stacktrace.yo.jconductor.core.dispatch.store.ResultStore;
import org.stacktrace.yo.jconductor.core.dispatch.store.ResultStoringDispatcher;
import org.stacktrace.yo.jconductor.core.dispatch.work.CompletedWork;
import org.stacktrace.yo.jconductor.core.dispatch.work.ScheduledWork;
import org.stacktrace.yo.jconductor.core.execution.job.SynchronousJob;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListenerBuilder;
import org.stacktrace.yo.jconductor.core.execution.work.Job;
import org.stacktrace.yo.jconductor.core.util.EmittingQueue;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ConsumerDispatcher implements SchedulingDispatcher, ResultStoringDispatcher {

    private static final Logger myLogger = LoggerFactory.getLogger(ConsumerDispatcher.class.getSimpleName());

    private final EmittingQueue<ScheduledWork> jobQueue;
    private final ResultStore myResultStore;
    private final ScheduledExecutorService executorService;
    private final ScheduledExecutorService schedulerService;
    private final AtomicInteger pending;
    private final AtomicInteger running;


    public ConsumerDispatcher() {
        this.jobQueue = createQueue();
        this.myResultStore = new InMemoryResultStore();
        this.pending = new AtomicInteger(0);
        this.running = new AtomicInteger(0);
        // two threads, 1 consumer and 1 reporter
        executorService = Executors.newSingleThreadScheduledExecutor();
        schedulerService = Executors.newSingleThreadScheduledExecutor();

        schedulerService.scheduleAtFixedRate(this.startReporter(), 0, 5, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(this.startConsumer(), 0, 100, TimeUnit.MILLISECONDS);
    }

    public ConsumerDispatcher(int consumers) {
        this.jobQueue = createQueue();
        this.myResultStore = new InMemoryResultStore();
        this.pending = new AtomicInteger(0);
        this.running = new AtomicInteger(0);
        // 1 thread per consumer
        // 1 fixed reporting/health thread
        executorService = Executors.newScheduledThreadPool(consumers);
        schedulerService = Executors.newSingleThreadScheduledExecutor();

        schedulerService.scheduleAtFixedRate(this.startReporter(), 0, 5, TimeUnit.SECONDS);
        IntStream.range(0, consumers)
                .forEach(value -> {
                    executorService.scheduleAtFixedRate(this.startConsumer(), 0, 100, TimeUnit.MILLISECONDS);
                });
    }

    public <T, V> String schedule(Job<T, V> job, T params) {
        String id = UUID.randomUUID().toString();
        ScheduledWork<T, V> scheduledWork = new ScheduledWork<>(job, params, id);
        myLogger.debug("[ConsumerDispatcher] scheduling new job {}", id);
        return jobQueue.offer(scheduledWork) ? id : "Unable To Queue";
    }


    @Override
    public <T, V> String schedule(Job<T, V> job, T params, StageListener<V> listener) {
        String id = UUID.randomUUID().toString();
        ScheduledWork<T, V> scheduledWork = new ScheduledWork<>(job, params, id, listener);
        myLogger.debug("[ConsumerDispatcher] scheduling new job {}", id);
        return jobQueue.offer(scheduledWork) ? id : "Unable To Queue";
    }

    @SuppressWarnings("unchecked")
    public void consume() {
        ScheduledWork work = this.jobQueue.poll();
        if (work != null) {
            myLogger.debug("[ConsumerDispatcher] Worker {} - Job Found: {}", Thread.currentThread().getName(), work.getId());
            SynchronousJob createdJob = createJob(work);
            createdJob.run();
        } else {
            myLogger.debug("[ConsumerDispatcher] Nothing in Queue");
        }
    }

    private Runnable startConsumer() {
        return () -> {
            try {
                if (jobQueue.peek() != null) {
                    myLogger.debug("[ConsumerDispatcher] Consuming");
                    consume();
                }
            } catch (Exception e) {
                myLogger.error("[ConsumerDispatcher] Queue Thread Error Restarting");
            }
        };
    }

    private Runnable startReporter() {
        return () -> {
            myLogger.debug("[ConsumerDispatcher] Report");
            try {
                myLogger.debug("[ConsumerDispatcher] Pending: {} ", this.pending.toString());
                myLogger.debug("[ConsumerDispatcher] Running: {} ", this.running.toString());
            } catch (Exception e) {
                myLogger.error("[ConsumerDispatcher] Report Thread Errored");
            }
        };
    }

    @Override
    public Optional<CompletedWork> fetch(String id) {
        return myResultStore.getResult(id);
    }

    public boolean shutdown() {
        myLogger.debug("[ConsumerDispatcher] Shutting Down");
        if (this.isRunning()) {
            myLogger.debug("[ConsumerDispatcher] Waiting for Work to finish");
            while (this.isRunning()) {
            }
        }
        myLogger.debug("[ConsumerDispatcher] Pending: {} ", this.pending.toString());
        myLogger.debug("[ConsumerDispatcher] Running: {} ", this.running.toString());
        myLogger.debug("[ConsumerDispatcher] Shutting Down");
        this.executorService.shutdown();
        this.schedulerService.shutdown();
        while (this.isActive()) {
        }
        myLogger.debug("[ConsumerDispatcher] Shut Down Gracefully");
        return true;
    }


    @Override
    public ScheduledExecutorService scheduler() {
        return schedulerService;
    }

    @Override
    public ResultStore getResultStore() {
        return myResultStore;
    }

    @SuppressWarnings("unchecked")
    private <T, V> SynchronousJob createJob(ScheduledWork<T, V> work) {
        return new SynchronousJob(work.getId(), work.getJob(), work.getParams(),
                new StageListenerBuilder<V>()
                        .bindListener(work.getListener())
                        .onStart(running -> {
                            myLogger.debug("[ConsumerDispatcher] Job Started: {}", work.getId());
                        })
                        .onComplete(
                                completed -> {
                                    myLogger.debug("[ConsumerDispatcher] Job Completed: {}", work.getId());
                                    myResultStore.putResult(work.getId(),
                                            new CompletedWork(
                                                    completed.getStageResult(),
                                                    work.getParams(),
                                                    work.getJob().getClass().toGenericString(),
                                                    completed.getId()
                                            )
                                    );
                                    this.running.getAndDecrement();
                                })
                        .onError(
                                error -> {
                                    myLogger.error("[ConsumerDispatcher] Job Errored: {}", work.getId(), error);
                                    myResultStore.putResult(work.getId(),
                                            new CompletedWork(
                                                    error,
                                                    work.getParams(),
                                                    work.getJob().getClass().toGenericString(),
                                                    work.getId()
                                            )
                                    );
                                    this.running.getAndDecrement();
                                })
                        .build()
        );
    }

    private EmittingQueue<ScheduledWork> createQueue() {
        return new EmittingQueue<>(
                queue -> {
                    this.pending.getAndIncrement();
                },
                dequeue -> {
                    this.pending.getAndDecrement();
                    this.running.getAndIncrement();
                }
        );
    }

    public boolean isRunning() {
        return this.pending.get() > 0 || this.running.get() > 0;
    }

    public boolean isActive() {
        return !this.executorService.isShutdown() && !this.schedulerService.isShutdown();
    }
}
