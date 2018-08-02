package org.stacktrace.yo.jconductor.core.util.supplier;

import java.util.Collection;

public class MultiLazyLoading<Value> implements MultiSupplier<Value> {

    public static <Value> MultiLazyLoading<Value> of(Collection<Value> inputCollection) {
        return new MultiLazyLoading<>(BlockingMultiSupplier.of(inputCollection));
    }

    public static <Value> MultiLazyLoading<Value> of(Value... inputs) {
        return new MultiLazyLoading<>(BlockingMultiSupplier.of(inputs));
    }

    public static <Value> MultiLazyLoading<Value> of(Value input) {
        return new MultiLazyLoading<>(BlockingMultiSupplier.of(input));
    }

    public static <Value> MultiLazyLoading<Value> of(MultiSupplier<Value> input) {
        return new MultiLazyLoading<>(input);
    }

    // no serialization
    private transient volatile boolean initialized;
    private transient final MultiSupplier<Value> mySupplierDelegate;

    private MultiLazyLoading(MultiSupplier<Value> sup) {
        mySupplierDelegate = sup;
    }

    @Override
    // http://en.wikipedia.org/wiki/Double-checked_locking
    public Value get() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    Value val = mySupplierDelegate.get();
                    initialized = true;
                    return val;
                }
            }
        } else {
            if (!mySupplierDelegate.finished()) {
                return mySupplierDelegate.get();
            }
        }
        return null;
    }

    @Override
    public boolean finished() {
        return mySupplierDelegate.finished();
    }

    @Override
    public Collection<Value> getRemaining() {
        return mySupplierDelegate.getRemaining();
    }

    @Override
    public String toString() {
        return mySupplierDelegate.toString();
    }
}

