package org.agentic4j.main;

import org.agentic4j.api.Gatekeeper;
import org.agentic4j.api.Message;

import java.util.function.Predicate;

public class AgenticWorkflowBuilder {

    private AgenticGraph graph;
    private Predicate<Message> circuitBreaker = (message) -> false;
    private String terminalAgent;
    private Gatekeeper gatekeeper = (message) -> true;
    private Boolean asyncMode = false;

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

    public AgenticWorkflowBuilder setGatekeeper(Gatekeeper gatekeeper) {
        this.gatekeeper = gatekeeper;
        return this;
    }

    public AgenticWorkflowBuilder asyncProcessing() {
        this.asyncMode = true;
        return this;
    }

    public AgenticWorkflow build() {
        return new AgenticWorkflow(graph, circuitBreaker, terminalAgent, gatekeeper, asyncMode);
    }
}
