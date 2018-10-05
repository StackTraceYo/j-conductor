package org.stacktrace.yo.jconductor.core.dispatch.store;

import org.stacktrace.yo.jconductor.core.dispatch.work.CompletedWork;

import java.util.Optional;

public interface ResultStore {

    Optional<CompletedWork> getResult(String id);

    boolean putResult(String id, CompletedWork completed);
}
