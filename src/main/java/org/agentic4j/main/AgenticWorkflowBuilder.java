package org.agentic4j.main;

import org.agentic4j.api.Message;
import org.agentic4j.utils.Utils;

import java.util.function.Predicate;

public class AgenticWorkflowBuilder {

    private AgenticGraph graph;
    private Predicate<Message> circuitBreaker = Utils.NO_CIRCUIT_BREAKER;
    private String terminalAgent;

    public AgenticWorkflowBuilder setGraph(AgenticGraph graph) {
        this.graph = graph;
        return this;
    }

    public AgenticWorkflowBuilder setCircuitBreaker(Predicate<Message> circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
        return this;
    }

    public AgenticWorkflowBuilder setTerminalAgent(String terminalAgent) {
        this.terminalAgent = terminalAgent;
        return this;
    }

    public AgenticWorkflow build() {
        return new AgenticWorkflow(graph, circuitBreaker, terminalAgent);
    }
}
