package org.stacktrace.yo.jconductor.reactive.util.observable;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.stacktrace.yo.jconductor.core.util.async.Futures;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ObservablesTest {

    private Scheduler scheduler;

    @Before
    public void setUp() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(5,
                new ThreadFactoryBuilder()
                        .setNameFormat("TestExecutor-%d")
                        .build());
        scheduler = Schedulers.from(executorService);
    }

    @After
    public void tearDown() throws Exception {
        scheduler.shutdown();
    }

    @Test
    public void futureGetsValueAndFinishes() {
        Observable<String> ob = Observable.create(subscriber -> {
            subscriber.onNext("Test");
            subscriber.onComplete();
        });

        CompletableFuture<String> converted = Observables.fromObservable(ob);
        assertEquals(converted.join(), "Test");
        assertTrue(converted.isDone());
    }

    @Test
    public void futureGetsOneValueAndFinishes() {
        Observable<String> ob = Observable.create(subscriber -> {
            subscriber.onNext("Test");
            subscriber.onNext("Test-2"); // output 2
            subscriber.onComplete();
        });

        CompletableFuture<String> converted = Observables.fromObservable(ob);
        assertEquals(converted.join(), "Test"); // value is the first only
        assertTrue(converted.isDone());
    }

    @Test
    public void futureCanBeChained() {
        Observable<String> ob = Observable.create(subscriber -> {
            subscriber.onNext("Test");
            subscriber.onComplete();
        });

        CompletableFuture<String> converted = Observables.fromObservable(ob).thenApply(s -> s + "-chain");
        assertEquals(converted.join(), "Test-chain"); // value is the first only
    }

    @Test
    public void slowObservable() {
        Observable<String> ob = Observable.create(subscriber -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            subscriber.onNext("Test");
            subscriber.onComplete();
        });

        CompletableFuture<String> converted = Observables.fromObservable(ob).thenApply(s -> s + "-chain");
        assertEquals(converted.join(), "Test-chain"); // value is the first only
    }

    @Test
    public void futureRunsOnSubscribedThread() {
        Observable<String> ob = Observable.create(subscriber -> {
            subscriber.onNext(Thread.currentThread().getName());
            subscriber.onComplete();
        });

        CompletableFuture<String> converted = Observables.fromObservable(ob, scheduler);
        assertEquals(converted.join(), "TestExecutor-0"); // value is the first only
    }

    @Test
    public void ObservablesRunsOnSubscribedThread() {
        Observable<String> ob = Observable.create(subscriber -> {
            subscriber.onNext(Thread.currentThread().getName());
            subscriber.onComplete();
        });

        Observable<String> ob2 = Observable.create(subscriber -> {
            subscriber.onNext(Thread.currentThread().getName());
            subscriber.onComplete();
        });

        Observable<String> ob3 = Observable.create(subscriber -> {
            subscriber.onNext(Thread.currentThread().getName());
            subscriber.onComplete();
        });

        Observable<String> ob4 = Observable.create(subscriber -> {
            subscriber.onNext(Thread.currentThread().getName());
            subscriber.onComplete();
        });

        // 4  on different threads all complete on different threads when converted to Observables
        Set<String> collect = Stream.of(
                Observables.fromObservable(ob, scheduler), Observables.fromObservable(ob2, scheduler),
                Observables.fromObservable(ob3, scheduler), Observables.fromObservable(ob4, scheduler))
                .map(CompletableFuture::join)
                .collect(Collectors.toSet());
        assertEquals(4, collect.size());
    }

    @Test
    public void slowObservablesJoinOnDifferentThreads() {
        List<String> orderedExecution = Lists.newArrayList();

        Observable<String> ob = Observable.create(subscriber -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            orderedExecution.add("f1");
            subscriber.onNext("s1");
            subscriber.onComplete();
        });

        Observable<String> ob2 = Observable.create(subscriber -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            orderedExecution.add("f2");
            subscriber.onNext("s2");
            subscriber.onComplete();
        });

        Observable<String> ob3 = Observable.create(subscriber -> {
            orderedExecution.add("f3");
            subscriber.onNext("s3");
            subscriber.onComplete();
        });

        Observable<String> ob4 = Observable.create(subscriber -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
            orderedExecution.add("f4");
            subscriber.onNext("s4");
            subscriber.onComplete();
        });

        // 4  on different threads all complete on different threads when converted to Observables
        List<String> collect = Stream.of(
                Observables.fromObservable(ob, scheduler), Observables.fromObservable(ob2, scheduler),
                Observables.fromObservable(ob3, scheduler), Observables.fromObservable(ob4, scheduler))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        assertEquals(4, collect.size());
        // submission order
        assertEquals(Lists.newArrayList("s1", "s2", "s3", "s4"), collect);
        // response order
        assertEquals(Lists.newArrayList("f3", "f2", "f4", "f1"), orderedExecution);
    }

    @Test
    public void singleFutureFromMultiple() {
        List<String> orderedExecution = Lists.newArrayList();

        Observable<String> ob = Observable.create(subscriber -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            orderedExecution.add("f1");
            subscriber.onNext("s1");
            subscriber.onComplete();
        });

        Observable<String> ob2 = Observable.create(subscriber -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            orderedExecution.add("f2");
            subscriber.onNext("s2");
            subscriber.onComplete();
        });

        Observable<String> ob3 = Observable.create(subscriber -> {
            orderedExecution.add("f3");
            subscriber.onNext("s3");
            subscriber.onComplete();
        });

        Observable<String> ob4 = Observable.create(subscriber -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
            orderedExecution.add("f4");
            subscriber.onNext("s4");
            subscriber.onComplete();
        });

        // 4  on different threads all complete on different threads when converted to Observables
        List<CompletableFuture<String>> futures = Lists.newArrayList(
                Observables.fromObservable(ob, scheduler), Observables.fromObservable(ob2, scheduler),
                Observables.fromObservable(ob3, scheduler), Observables.fromObservable(ob4, scheduler));

        List<String> joined = Futures.toFutureList(futures)
                .join();
        assertEquals(4, joined.size());
        // submission order
        assertEquals(Lists.newArrayList("s1", "s2", "s3", "s4"), joined);
        // response order
        assertEquals(Lists.newArrayList("f3", "f2", "f4", "f1"), orderedExecution);
    }
}