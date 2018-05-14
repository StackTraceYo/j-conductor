package org.stacktrace.yo.jconductor.core.dispatch;

import org.stacktrace.yo.jconductor.core.job.AsynchronousJob;
import org.stacktrace.yo.jconductor.core.job.Work;
import org.stacktrace.yo.jconductor.core.job.stage.StageListener;
import org.stacktrace.yo.jconductor.core.job.stage.StageListenerBuilder;
import org.stacktrace.yo.jconductor.core.util.EmittingQueue;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncDispatcher implements Dispatcher {

    private final EmittingQueue<ScheduledWork> jobQueue;
    private final ConcurrentHashMap<String, ScheduledWork> running;
    private final ConcurrentHashMap<String, CompletedWork> completed;
    private final DispatcherConfig config;
    private final ExecutorService executor;


    public AsyncDispatcher(DispatcherConfig config) {
        this.jobQueue = createQueue();
        this.config = config;
        this.running = new ConcurrentHashMap<>();
        this.completed = new ConcurrentHashMap<>();
        this.executor = Executors.newFixedThreadPool(config.getMaxConcurrent());
    }

    public <T, V> String schedule(Work<T, V> job, T params) {
        String id = UUID.randomUUID().toString();
        ScheduledWork<T, V> scheduledWork = new ScheduledWork<>(job, params, id);
        return jobQueue.offer(scheduledWork) ? id : "Unable To Queue";
    }


    @Override
    public <T, V> String schedule(Work<T, V> job, T params, StageListener<V> listener) {
        return null;
    }

    @Override
    public void consume() {
        ScheduledWork work;
        if (this.jobQueue.peek() != null) {
            work = this.jobQueue.poll();
            AsynchronousJob createdJob = createAsyncJob(work);
            createdJob.run(executor);
        }
    }

    @SuppressWarnings("unchecked")
    private AsynchronousJob createAsyncJob(ScheduledWork work) {
        return new AsynchronousJob<>(work.getId(), work.getWork(), work.getParams(),
                new StageListenerBuilder()
                        .onStart(running -> this.running.put(work.getId(), work))
                        .onComplete(
                                completed -> {
                                    this.completed.put(work.getId(),
                                            new CompletedWork(
                                                    completed.getStageResult(),
                                                    work.getParams(),
                                                    work.getWork().getClass().toGenericString(),
                                                    completed.getId()
                                            )
                                    );
                                    this.running.remove(work.getId());
                                })
                        .onError(
                                error -> {
                                    this.completed.put(work.getId(),
                                            new CompletedWork(
                                                    error,
                                                    work.getParams(),
                                                    work.getWork().getClass().toGenericString(),
                                                    work.getId()
                                            )
                                    );
                                    this.running.remove(work.getId());
                                })
                        .build()
        );
    }


    private EmittingQueue<ScheduledWork> createQueue() {
        return new EmittingQueue<>(
                queue -> {
                    this.consume();
                },
                dequeue -> {

                }
        );
    }
}
