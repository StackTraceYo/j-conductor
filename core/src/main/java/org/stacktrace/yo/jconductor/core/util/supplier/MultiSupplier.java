package org.stacktrace.yo.jconductor.core.util.supplier;

import java.util.Collection;
import java.util.function.Supplier;

public interface MultiSupplier<T> extends Supplier<T> {

    boolean finished();

    Collection<T> getRemaining();
}

