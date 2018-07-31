package org.stacktrace.yo.jconductor.core.execution.flow;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

public class Flow<Input, Output> {

    MutableValueGraph<FlowStep, Boolean> myFlow = ValueGraphBuilder
            .directed()
            .build();



    public void add(FlowStep step) {
        myFlow.addNode(step);
    }

}
