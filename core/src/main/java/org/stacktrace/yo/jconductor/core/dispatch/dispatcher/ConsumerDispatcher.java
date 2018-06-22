package org.stacktrace.yo.jconductor.core.dispatch.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stacktrace.yo.jconductor.core.dispatch.work.CompletedWork;
import org.stacktrace.yo.jconductor.core.dispatch.work.ScheduledWork;
import org.stacktrace.yo.jconductor.core.execution.job.SynchronousJob;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListenerBuilder;
import org.stacktrace.yo.jconductor.core.execution.work.Job;
import org.stacktrace.yo.jconductor.core.util.EmittingQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ConsumerDispatcher implements Dispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerDispatcher.class.getSimpleName());

    private final EmittingQueue<ScheduledWork> jobQueue;
    private final Map<String, CompletedWork> completed;
    private final ScheduledExecutorService executorService;
    private final ScheduledExecutorService reportExecutor;
    private final AtomicInteger pending;
    private final AtomicInteger running;


    public ConsumerDispatcher() {
        this.jobQueue = createQueue();
        this.completed = new ConcurrentHashMap<>();
        this.pending = new AtomicInteger(0);
        this.running = new AtomicInteger(0);
        // two threads, 1 consumer and 1 reporter
        executorService = Executors.newSingleThreadScheduledExecutor();
        reportExecutor = Executors.newSingleThreadScheduledExecutor();

        reportExecutor.scheduleAtFixedRate(this.startReporter(), 0, 5, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(this.startConsumer(), 0, 100, TimeUnit.MILLISECONDS);
    }

    public ConsumerDispatcher(int consumers) {
        this.jobQueue = createQueue();
        this.completed = new HashMap<>();
        this.pending = new AtomicInteger(0);
        this.running = new AtomicInteger(0);
        // 1 thread per consumer
        // 1 fixed reporting/health thread
        executorService = Executors.newScheduledThreadPool(consumers);
        reportExecutor = Executors.newSingleThreadScheduledExecutor();

        reportExecutor.scheduleAtFixedRate(this.startReporter(), 0, 5, TimeUnit.SECONDS);
        IntStream.range(0, consumers)
                .forEach(value -> {
                    executorService.scheduleAtFixedRate(this.startConsumer(), 0, 100, TimeUnit.MILLISECONDS);
                });
    }

    public <T, V> String schedule(Job<T, V> job, T params) {
        String id = UUID.randomUUID().toString();
        ScheduledWork<T, V> scheduledWork = new ScheduledWork<>(job, params, id);
        LOGGER.debug("[ConsumerDispatcher] scheduling new job {}", id);
        return jobQueue.offer(scheduledWork) ? id : "Unable To Queue";
    }


    @Override
    public <T, V> String schedule(Job<T, V> job, T params, StageListener<V> listener) {
        String id = UUID.randomUUID().toString();
        ScheduledWork<T, V> scheduledWork = new ScheduledWork<>(job, params, id, listener);
        LOGGER.debug("[ConsumerDispatcher] scheduling new job {}", id);
        return jobQueue.offer(scheduledWork) ? id : "Unable To Queue";
    }

    @Override
    @SuppressWarnings("unchecked")
    public void consume() {
        ScheduledWork work = this.jobQueue.poll();
        if (work != null) {
            LOGGER.debug("[ConsumerDispatcher] Worker {} - Job Found: {}", Thread.currentThread().getName(), work.getId());
            SynchronousJob createdJob = createJob(work);
            createdJob.run();
        } else {
            LOGGER.debug("[ConsumerDispatcher] Nothing in Queue");
        }
    }

    private Runnable startConsumer() {
        return () -> {
            try {
                if (jobQueue.peek() != null) {
                    LOGGER.debug("[ConsumerDispatcher] Consuming");
                    consume();
                }
            } catch (Exception e) {
                LOGGER.error("[ConsumerDispatcher] Queue Thread Error Restarting");
            }
        };
    }

    private Runnable startReporter() {
        return () -> {
            LOGGER.debug("[ConsumerDispatcher] Report");
            try {
                LOGGER.debug("[ConsumerDispatcher] Pending: {} ", this.pending.toString());
                LOGGER.debug("[ConsumerDispatcher] Running: {} ", this.running.toString());
            } catch (Exception e) {
                LOGGER.error("[ConsumerDispatcher] Report Thread Errored");
            }
        };
    }

    @Override
    public CompletedWork fetch(String id) {
        return completed.get(id);
    }

    public boolean shutdown() {
        LOGGER.debug("[ConsumerDispatcher] Shutting Down");
        if (this.isRunning()) {
            LOGGER.debug("[ConsumerDispatcher] Waiting for Work to finish");
            while (this.isRunning()) {
            }
        }
        LOGGER.debug("[ConsumerDispatcher] Pending: {} ", this.pending.toString());
        LOGGER.debug("[ConsumerDispatcher] Running: {} ", this.running.toString());
        LOGGER.debug("[ConsumerDispatcher] Shutting Down");
        this.executorService.shutdown();
        this.reportExecutor.shutdown();
        while (this.isActive()) {
        }
        LOGGER.debug("[ConsumerDispatcher] Shut Down Gracefully");
        return true;
    }

    @SuppressWarnings("unchecked")
    private <T, V> SynchronousJob createJob(ScheduledWork<T, V> work) {
        return new SynchronousJob(work.getId(), work.getJob(), work.getParams(),
                new StageListenerBuilder<V>()
                        .bindListener(work.getListener())
                        .onStart(running -> {
                            LOGGER.debug("[ConsumerDispatcher] Job Started: {}", work.getId());
                        })
                        .onComplete(
                                completed -> {
                                    LOGGER.debug("[ConsumerDispatcher] Job Completed: {}", work.getId());
                                    this.completed.put(work.getId(),
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
                                    LOGGER.error("[ConsumerDispatcher] Job Errored: {}", work.getId(), error);
                                    this.completed.put(work.getId(),
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
        return !this.executorService.isShutdown() && !this.reportExecutor.isShutdown();
    }

}