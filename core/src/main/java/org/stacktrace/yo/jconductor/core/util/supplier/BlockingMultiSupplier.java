package org.stacktrace.yo.jconductor.core.util.supplier;

import com.google.common.collect.Queues;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;

public class BlockingMultiSupplier<T> implements MultiSupplier<T> {

    private final BlockingQueue<T> myQueue;
    private volatile boolean finished;


    public static <T> BlockingMultiSupplier<T> of(Collection<T> inputCollection) {
        return new BlockingMultiSupplier<>(inputCollection);
    }

    public static <T> BlockingMultiSupplier<T> of(T... inputs) {
        return new BlockingMultiSupplier<>(Arrays.asList(inputs));
    }

    public static <T> BlockingMultiSupplier<T> of(T input) {
        return new BlockingMultiSupplier<>(Collections.singleton(input));
    }

    private BlockingMultiSupplier(Collection<T> inputCollection) {
        myQueue = Queues.newLinkedBlockingQueue(inputCollection);
    }

    @Override
    public T get() {
        T object = null;
        if (!finished()) {
            object = myQueue.poll();
            if (object == null) {
                finished = true;
            }
        }
        return object;
    }

    @Override
    public boolean finished() {
        synchronized (this) {
            return finished;
        }
    }

    @Override
    public Collection<T> getRemaining() {
        Collection<T> remaining = Collections.emptyList();
        if (!finished()) {
            finished = true;
            myQueue.drainTo(remaining);
        }
        return remaining;
    }

    public MultiLazyLoading<T> lazy() {
        return MultiLazyLoading.of(this);
    }
}

