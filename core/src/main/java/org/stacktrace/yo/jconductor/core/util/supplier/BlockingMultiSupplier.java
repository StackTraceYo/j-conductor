package org.stacktrace.yo.jconductor.core.util.supplier;

import com.google.common.collect.Queues;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;

public class BlockingMultiSupplier<T> implements MultiSupplier<T> {

    private final BlockingQueue<T> myQueue;
    private volatile boolean finished;


    public static <T> BlockingMultiSupplier<T> multiSupplier(Collection<T> inputCollection) {
        return new BlockingMultiSupplier<>(inputCollection);
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
}

