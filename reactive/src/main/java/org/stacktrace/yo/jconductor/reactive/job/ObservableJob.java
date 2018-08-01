package org.stacktrace.yo.jconductor.reactive.job;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;
import org.stacktrace.yo.jconductor.core.execution.job.Executable;
import org.stacktrace.yo.jconductor.core.execution.job.Worker;
import org.stacktrace.yo.jconductor.core.execution.stage.JobExecutionStage;
import org.stacktrace.yo.jconductor.core.execution.stage.JobStage;
import org.stacktrace.yo.jconductor.core.execution.work.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public class ObservableJob<T, V> extends Worker<T, V> implements Executable<Observable<JobStage>> {


    public ObservableJob(String id, Job<T, V> job, T params) {
        super(id, job, params);
    }

    public ObservableJob(String id, Work<T, V> work, T params) {
        super(id, work, params);
    }

    @Override
    public Observable<JobStage> run() {
        return createAsyncObservable();
    }

    @Override
    public Observable<JobStage> run(Executor e) {
        return createAsyncObservable(e);
    }

    private Observable<JobStage> createAsyncObservable() {
        return Observable.create(subscriber -> {
            subscriber.onNext(JobExecutionStage.INITIALZING.createStage(this.id));
            this.job.init(this.params);
            subscriber.onNext(JobExecutionStage.RUNNING.createStage(this.id));
            System.out.println(Thread.currentThread().getName());
            this.supplyFuture()
                    .whenComplete(finish(subscriber));
        });
    }

    private Observable<JobStage> createAsyncObservable(Executor e) {
        return Observable.create(subscriber -> {
            subscriber.onNext(JobExecutionStage.INITIALZING.createStage(this.id));
            this.job.init(this.params);
            subscriber.onNext(JobExecutionStage.RUNNING.createStage(this.id));
            System.out.println(Thread.currentThread().getName());
            this.supplyFuture(e)
                    .whenComplete(finish(subscriber));
        });
    }

    private BiConsumer<? super V, ? super Throwable> finish(ObservableEmitter<JobStage> subscriber) {
        return (v, throwable) -> {
            if (throwable != null) {
                this.status = JobExecutionStage.ERRORED;
                subscriber.onError(throwable);
                subscriber.onNext(this.status.createStage(this.id, throwable));
            } else {
                this.job.postRun();
                subscriber.onNext(JobExecutionStage.COMPLETE.createStage(this.id, result));
                subscriber.onComplete();
            }
        };
    }

    private CompletableFuture<V> supplyFuture() {
        return CompletableFuture.supplyAsync(() -> this.job.doWork(this.params));
    }

    private CompletableFuture<V> supplyFuture(Executor e) {
        return CompletableFuture.supplyAsync(() -> this.job.doWork(this.params), e);
    }

    public static void main(String args[]) throws InterruptedException {

        new ObservableJob<>("test_id", params1 -> {
            try {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Finished";
        }, "test params")
                .run()
                .observeOn(Schedulers.io())
                .subscribe(s -> {
                            System.out.println(s);
                            System.out.println(Thread.currentThread().getName());
                        },
                        throwable -> System.out.println(throwable.getMessage()),
                        () -> {
                            System.out.println(Thread.currentThread().getName());
                            System.out.println("Completed 2");
                        });
        Thread.sleep(10000);


    }
}

