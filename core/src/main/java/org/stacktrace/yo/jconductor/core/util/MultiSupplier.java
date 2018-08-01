package org.stacktrace.yo.jconductor.core.util;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class MultiSupplier<T> implements Supplier<T> {

    private final List<T> myList;
    private final AtomicInteger myCounter;


    public static <T> MultiSupplier<T> multiSupplier(Collection<T> stuff) {
        return new MultiSupplier<>(stuff);
    }

    private MultiSupplier(Collection<T> stuff) {
        myList = Lists.newArrayList(stuff);
        myCounter = new AtomicInteger(0);
//        finshed = new AtomicBoolean(false);
    }

    @Override
    public T get() {
        return null;
    }
}

