package org.stacktrace.yo.jconductor.core.job;

import org.stacktrace.yo.jconductor.core.work.PostRun;
import org.stacktrace.yo.jconductor.core.work.PreStart;
import org.stacktrace.yo.jconductor.core.work.Work;


public interface Job<T, V> extends PreStart<T>, PostRun, Work<T, V> {
}

