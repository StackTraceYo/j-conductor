package org.stacktrace.yo.jconductor.reactive.job;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;
import org.stacktrace.yo.jconductor.core.job.AsyncJob;
import org.stacktrace.yo.jconductor.core.job.stage.JobExecutionStage;
import org.stacktrace.yo.jconductor.core.job.stage.JobStage;

import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public abstract class ObservableJob<T, V> extends AsyncJob<T, V> {

    public ObservableJob(String id, T params) {
        super(id, params);
    }

    public final Observable<JobStage> observeAsync() {
        return this.createAsyncObservable();
    }

    public final Observable<JobStage> observeAsync(Executor e) {
        return this.createAsyncObservable();
    }

    private Observable<JobStage> createAsyncObservable() {
        return Observable.create(subscriber -> {
            subscriber.onNext(JobExecutionStage.INITIALZING.createStage(this.id));
            this.init();
            this.init(this.params);
            subscriber.onNext(JobExecutionStage.STARTED.createStage(this.id));
            subscriber.onNext(JobExecutionStage.RUNNING.createStage(this.id));
            System.out.println(Thread.currentThread().getName());
            this.supplyFuture()
                    .whenComplete(finish(subscriber));
        });
    }

    private Observable<JobStage> createAsyncObservable(Executor e) {
        return Observable.create(subscriber -> {
            subscriber.onNext(JobExecutionStage.INITIALZING.createStage(this.id));
            this.init();
            this.init(this.params);
            subscriber.onNext(JobExecutionStage.STARTED.createStage(this.id));
            subscriber.onNext(JobExecutionStage.RUNNING.createStage(this.id));
            System.out.println(Thread.currentThread().getName());
            this.supplyFuture(e)
                    .whenComplete(finish(subscriber));
        });
    }

    private BiConsumer<? super V, ? super Throwable> finish(ObservableEmitter<JobStage> subscriber) {
        return (v, throwable) -> {
            if (throwable != null) {
                this.errored = true;
                subscriber.onError(throwable);
                subscriber.onNext(JobExecutionStage.ERRORED.createStage(this.id, throwable));
            } else {
                this.cleanup();
                subscriber.onNext(JobExecutionStage.COMPLETE.createStage(this.id, result));
                subscriber.onComplete();
            }
        };
    }

    public static void main(String args[]) throws InterruptedException {

        new ObservableJob<String, String>("testid", "Params") {

            @Override
            public void init() {

                System.out.println("Initializing");
            }

            @Override
            public void init(String params) {
                System.out.println("Initializing With Params");
            }

            @Override
            public String execute(String params) {
                try {
                    System.out.println(Thread.currentThread().getName());
                    System.out.println("Starting");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "finished 2";
            }

            @Override
            public void cleanup() {
                System.out.print("Cleanup");
            }
        }
                .observeAsync()
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

