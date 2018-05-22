package remote.work.core;

import org.stacktrace.yo.jconductor.core.dispatch.dispatcher.ConsumerDispatcher;
import org.stacktrace.yo.jconductor.core.dispatch.dispatcher.Dispatcher;

public class WorkNode {

    private final Dispatcher dispatcher;


    public WorkNode() {
        dispatcher = new ConsumerDispatcher(10);
    }
}
