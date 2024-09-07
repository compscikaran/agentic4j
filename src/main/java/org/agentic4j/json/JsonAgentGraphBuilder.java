package org.agentic4j.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import lombok.Getter;
import org.agentic4j.api.Agent;
import org.agentic4j.api.Gatekeeper;
import org.agentic4j.main.AgenticGraph;
import org.agentic4j.main.AgenticWorkflow;
import org.agentic4j.main.AgenticWorkflowBuilder;
import org.agentic4j.utils.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class JsonAgentGraphBuilder {

    private final AgenticWorkflow workflow;
    private final ChatLanguageModel model;
    private final ChatMemory memory;

    public JsonAgentGraphBuilder(String json, ChatLanguageModel model, ChatMemory memory) {
        this.model = model;
        this.memory = memory;
        JsonObject input = JsonParser.parseString(json).getAsJsonObject();
        JsonObject graph = input.getAsJsonObject("graph");
        String gatekeeper = input.getAsJsonPrimitive("gatekeeper").getAsString();
        boolean asyncMode = input.getAsJsonPrimitive("asyncMode").getAsBoolean();
        int maxMessages = input.getAsJsonPrimitive("maxMessages").getAsInt();
        String terminalAgent = input.getAsJsonPrimitive("terminalAgent").getAsString();

        Map<String, JsonElement> mapper = graph.asMap();
        Map<String, List<String>> listeners = calculateListeners(mapper);
        AgenticGraph agenticGraph = new AgenticGraph();

        AgenticWorkflowBuilder builder = new AgenticWorkflowBuilder()
                .setGraph(agenticGraph)
                .setMessageTimeout(maxMessages)
                .setTerminalAgent(terminalAgent);

        if(StringUtils.isNoneBlank(gatekeeper)) {
            builder.setGatekeeper(createGatekeeper(gatekeeper).build());
        }

        if(asyncMode) {
            builder.asyncProcessing();
        }

        this.workflow = builder.build();
        buildAgenticGraph(agenticGraph, memory, mapper, listeners);
    }

    private void buildAgenticGraph(AgenticGraph agenticGraph, ChatMemory memory, Map<String, JsonElement> mapper, Map<String, List<String>> listeners) {
        for (String agentNode: mapper.keySet()) {
            JsonElement element = mapper.get(agentNode);
            String prompt = element.getAsJsonObject().get("prompt").getAsString();
            boolean workflowControl = element.getAsJsonObject().get("workflowControl").getAsBoolean();
            AiServices<Agent> agentBuilder = createAgent(prompt)
                    .chatLanguageModel(this.model)
                    .chatMemory(memory);
            Agent agent;
            if(workflowControl) {
                agent = agentBuilder.tools(workflow.getEndTool()).build();
            } else {
                agent = agentBuilder.build();
            }
            agenticGraph.addAgent(agentNode, agent, listeners.get(agentNode));
        }
        agenticGraph.addAgent(Constants.USER, null, listeners.get(Constants.USER));
    }

    private static Map<String, List<String>> calculateListeners(Map<String, JsonElement> mapper) {
        Map<String, List<String>> listeners = new HashMap<>();
        for (String agentNode: mapper.keySet()) {
            JsonElement element = mapper.get(agentNode);
            JsonArray listensTo = element.getAsJsonObject().get("listensTo").getAsJsonArray();
            List<String> listensToList = listensTo.asList().stream().map(JsonElement::getAsString).toList();
            for (String listensToNode: listensToList) {
                if(listeners.containsKey(listensToNode)) {
                    listeners.get(listensToNode).add(agentNode);
                } else {
                    listeners.put(listensToNode, new ArrayList<>());
                    listeners.get(listensToNode).add(agentNode);
                }
            }
        }
        return listeners;
    }

    public AiServices<Agent> createAgent(String prompt) {
        return AiServices.builder(Agent.class)
                .chatLanguageModel(model)
                .chatMemory(memory)
                .systemMessageProvider(chatMemoryId -> prompt);
    }

    public AiServices<Gatekeeper> createGatekeeper(String prompt) {
        return AiServices.builder(Gatekeeper.class)
                .chatLanguageModel(model)
                .chatMemory(memory)
                .systemMessageProvider(chatMemoryId -> prompt);
    }
}
