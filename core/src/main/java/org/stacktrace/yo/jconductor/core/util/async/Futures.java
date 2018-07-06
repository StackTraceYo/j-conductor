package org.stacktrace.yo.jconductor.core.util.async;


import com.google.common.annotations.VisibleForTesting;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Futures class has utility methods for dealing with various "Futures".
 */
public class Futures {

    /**
     * This method accepts a list of {@link CompletableFuture} and converts it into a single CompletableFuture
     * of a stream of the same type, that completes only when the list's futures complete.
     *
     * @param <T>        the type parameter
     * @param futureList the future list
     * @return the completable future
     */
    public static <T> CompletableFuture<Stream<T>> toFutureStream(List<CompletableFuture<T>> futureList) {
        return CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]))
                .thenApply(v -> futureList.stream().map(CompletableFuture::join));
    }

    /**
     * This method accepts a list of {@link CompletableFuture} and converts it into a single CompletableFuture
     * of the same type, that completes only when the list's futures complete
     *
     * @param <T>        the type parameter
     * @param futureList the future list
     * @return the completable future
     */
    public static <T> CompletableFuture<List<T>> toFutureList(List<CompletableFuture<T>> futureList) {
        return CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]))
                .thenApply(v -> futureList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

    /**
     * This method accepts a list of {@link CompletableFuture} and converts it into a single CompletableFuture
     * of a stream of the same type, that completes only when the list's futures complete.
     *
     * @param <T>        the type parameter
     * @param futureList the future list
     * @param ex         the executor to complete the futures on
     * @return the completable future
     */
    public static <T> CompletableFuture<Stream<T>> toFutureStream(List<CompletableFuture<T>> futureList, Executor ex) {
        return CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]))
                .thenApplyAsync(v -> futureList.stream().map(CompletableFuture::join), ex);
    }

    /**
     * This method accepts a list of {@link CompletableFuture} and converts it into a single CompletableFuture
     * of the same type, that completes only when the list's futures complete
     *
     * @param <T>        the type parameter
     * @param futureList the future list
     * @param ex         the executor to complete the futures
     * @return the completable future
     */
    public static <T> CompletableFuture<List<T>> toFutureList(List<CompletableFuture<T>> futureList, Executor ex) {
        return CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]))
                .thenApplyAsync(v -> futureList.stream().map(CompletableFuture::join).collect(Collectors.toList()), ex);
    }
}
