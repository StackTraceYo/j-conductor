package org.stacktrace.yo.jconductor.reactive.job;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.stacktrace.yo.jconductor.core.job.AbstractJob;

public abstract class ObservableJob<T, V> extends AbstractJob<T, V> {

    public ObservableJob(T params) {
        super(params);
    }

    public Observable<V> startAsync(String id) {
        return Observable.create(subscriber -> {
            this.started = true;
            this.result = this.execute(this.params);
            subscriber.onNext(this.result);
            this.completed = true;
            subscriber.onComplete();
        });
    }

    public static void main(String args[]) {

        new ObservableJob<String, String>("test") {
            @Override
            public String execute(String params) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "finished 2";
            }
        }
                .startAsync("now")
                .observeOn(Schedulers.computation())
                .subscribe(s -> System.out.print(s),
                        throwable -> System.out.print(throwable.getMessage()),
                        () -> System.out.println("Completed 2"));

        new ObservableJob<String, String>("test") {
            @Override
            public String execute(String params) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "finished";
            }
        }
                .startAsync("now")
                .subscribe(s -> System.out.print(s),
                        throwable -> System.out.print(throwable.getMessage()),
                        () -> System.out.println("Completed"));


    }
}

