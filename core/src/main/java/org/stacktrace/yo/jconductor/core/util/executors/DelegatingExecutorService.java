package org.stacktrace.yo.jconductor.core.util.executors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

public class DelegatingExecutorService extends AbstractExecutorService {

    private ExecutorService myDelegate;
    private boolean myIsShutdown;

    private int mySubmittedTasks = 0;

    private List<RunnableFuture<?>> myFutures = new ArrayList<>();

    public DelegatingExecutorService(ExecutorService delegate) {
        myDelegate = delegate;
    }

    private synchronized void taskStart() {
        if (myIsShutdown) {
            throw new RejectedExecutionException("Already shut down");
        }
        mySubmittedTasks++;
    }

    private synchronized void taskEnd() {
        mySubmittedTasks--;
    }

    @Override
    protected synchronized <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        Runnable trackedRunnable = () -> {
            taskStart();
            try {
                runnable.run();
            } finally {
                taskEnd();
            }
        };

        RunnableFuture<T> f = new FutureTask<>(trackedRunnable, value);
        myFutures.add(f);
        return f;
    }

    @Override
    protected synchronized <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        Callable<T> trackedCallable = () -> {
            taskStart();
            try {
                return callable.call();
            } finally {
                taskEnd();
            }
        };

        RunnableFuture<T> f = new FutureTask<>(trackedCallable);
        myFutures.add(f);
        return f;
    }

    @Override
    public synchronized void execute(Runnable runnable) {
        if (myIsShutdown) {
            throw new RejectedExecutionException("Already shut down");
        }

        if (runnable instanceof RunnableFuture && myFutures.contains(runnable)) {
            myDelegate.execute(runnable);
        } else {
            submit(runnable);
        }
    }

    @Override
    public synchronized void shutdown() {
        myIsShutdown = true;
    }

    @Override
    public synchronized List<Runnable> shutdownNow() {
        myIsShutdown = true;
        myFutures.forEach(f -> f.cancel(true));
        return Collections.emptyList();
    }

    @Override
    public synchronized boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long timeoutMillis = unit.toMillis(timeout);
        while ((!myIsShutdown || mySubmittedTasks > 0) && timeoutMillis > 0) {
            wait(1);
            timeoutMillis--;
        }

        return myIsShutdown && mySubmittedTasks == 0;
    }

    @Override
    public boolean isShutdown() {
        return myIsShutdown;
    }

    @Override
    public boolean isTerminated() {
        return myIsShutdown && mySubmittedTasks == 0;
    }
}
