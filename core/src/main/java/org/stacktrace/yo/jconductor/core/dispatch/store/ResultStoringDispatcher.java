package org.stacktrace.yo.jconductor.core.dispatch.store;

import org.stacktrace.yo.jconductor.core.dispatch.dispatcher.Dispatcher;
import org.stacktrace.yo.jconductor.core.dispatch.store.ResultStore;
import org.stacktrace.yo.jconductor.core.dispatch.work.CompletedWork;

import java.util.Optional;

public interface ResultStoringDispatcher extends Dispatcher {

    Optional<CompletedWork> fetch(String id);

    ResultStore getResultStore();
}
