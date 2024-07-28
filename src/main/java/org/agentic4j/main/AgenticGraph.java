package org.agentic4j.main;

import org.agentic4j.api.Agent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgenticGraph {

    private Map<String, List<String>> agentGraph;
    private Map<String, Agent> agentRepo;

    public AgenticGraph() {
        this.agentGraph = new HashMap<>();
        this.agentRepo = new HashMap<>();
    }

    public void addAgent(String agentName, Agent agent, List<String> listeners) {
        this.agentRepo.put(agentName, agent);
        this.agentGraph.put(agentName, listeners);
    }

    public void chatToAgent(String agentName, String message) {
        if(agentRepo.containsKey(agentName)) {
            agentRepo.get(agentName).chat(message);
        } else {
            throw new IllegalArgumentException("Agent does not exist");
        }
    }

    public Map<String, List<String>> getAgentGraph() {
        return agentGraph;
    }
}
