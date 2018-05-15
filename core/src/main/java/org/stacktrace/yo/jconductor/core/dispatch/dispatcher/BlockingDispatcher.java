package org.stacktrace.yo.jconductor.core.dispatch.dispatcher;

import org.stacktrace.yo.jconductor.core.dispatch.work.CompletedWork;
import org.stacktrace.yo.jconductor.core.dispatch.work.ScheduledWork;
import org.stacktrace.yo.jconductor.core.execution.job.SynchronousJob;
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener;
import org.stacktrace.yo.jconductor.core.execution.work.Job;
import org.stacktrace.yo.jconductor.core.util.EmittingQueue;

import java.util.HashMap;
import java.util.UUID;

public class BlockingDispatcher implements Dispatcher {

    private final EmittingQueue<ScheduledWork> jobQueue;
    private final HashMap<String, CompletedWork> completed;
    private ScheduledWork runningJob;


    public BlockingDispatcher() {
        this.jobQueue = createQueue();
        this.completed = new HashMap<>();
    }

    public <T, V> String schedule(Job<T, V> job, T params) {
        String id = UUID.randomUUID().toString();
        ScheduledWork<T, V> scheduledWork = new ScheduledWork<>(job, params, id);
        return jobQueue.offer(scheduledWork) ? id : "Unable To Queue";
    }


    @Override
    public <T, V> String schedule(Job<T, V> job, T params, StageListener<V> listener) {
        String id = UUID.randomUUID().toString();
        ScheduledWork<T, V> scheduledWork = new ScheduledWork<>(job, params, id, listener);
        return jobQueue.offer(scheduledWork) ? id : "Unable To Queue";
    }

    @Override
    @SuppressWarnings("unchecked")
    public void consume() {
        ScheduledWork work = this.jobQueue.poll();
        if (work != null) {
            SynchronousJob createdJob = createJob(work);
            createdJob.run();
        }
    }

    @SuppressWarnings("unchecked")
    private <T, V> SynchronousJob createJob(ScheduledWork<T, V> work) {
        return new SynchronousJob(work.getId(), work.getJob(), work.getParams(),
                new StageListener.StageListenerBuilder<V>()
                        .bindListener(work.getListener())
                        .onStart(running -> this.runningJob = work)
                        .onComplete(
                                completed -> {
                                    this.completed.put(work.getId(),
                                            new CompletedWork(
                                                    completed.getStageResult(),
                                                    work.getParams(),
                                                    work.getJob().getClass().toGenericString(),
                                                    completed.getId()
                                            )
                                    );
                                    this.consume();
                                    this.runningJob = null;
                                })
                        .onError(
                                error -> {
                                    this.completed.put(work.getId(),
                                            new CompletedWork(
                                                    error,
                                                    work.getParams(),
                                                    work.getJob().getClass().toGenericString(),
                                                    work.getId()
                                            )
                                    );
                                    this.consume();
                                    this.runningJob = null;
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
