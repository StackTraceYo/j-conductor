package org.stacktrace.yo.jconductor.reactive.util.observable;

import com.google.common.annotations.VisibleForTesting;
import io.reactivex.Observable;
import io.reactivex.Scheduler;

import java.util.concurrent.CompletableFuture;

public class Observables {

    /**
     * This method accepts a {@link Observable} and converts it to a {@link CompletableFuture}
     * of the same return type.
     * <p>
     * The future is subscribed on the thread the observable is created on.
     * The future only completes once, meaning any observable that pushes more
     * than one value will not have any effect, the first one will be the result of the
     * future.
     * <p>
     * The future completes with an exception when the observable throws an error.
     *
     * @param <T>        the type parameter
     * @param observable the observable
     * @return the completable future
     */
    public static <T> CompletableFuture<T> fromObservable(Observable<T> observable) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        observable
                .subscribe(
                        future::complete,
                        future::completeExceptionally
                );
        return future;
    }

    /**
     * This method accepts a {@link Observable} and converts it to a {@link CompletableFuture}
     * of the same return type.
     * <p>
     * The future is subscribed on a provided {@link Scheduler}
     * <p>
     * this is more testing only, it was needed to mimic getting an observable from a different thread.
     *
     * @param <T>        the type parameter
     * @param observable the observable
     * @return the completable future
     */
    @VisibleForTesting
    static <T> CompletableFuture<T> fromObservable(Observable<T> observable, Scheduler scheduler) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        observable
                .subscribeOn(scheduler)
                .subscribe(
                        future::complete,
                        future::completeExceptionally
                );
        return future;
    }
}
