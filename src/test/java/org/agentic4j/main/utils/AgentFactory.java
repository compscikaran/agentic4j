package org.agentic4j.main.utils;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.agentic4j.api.Agent;

public class AgentFactory {

    private static final String apiKey = System.getenv("OPENAI_API_KEY");

    public static AiServices<Agent> createAgent(String type, String prompt, String modelName) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(apiKey)
                .build();
        return AiServices.builder(Agent.class)
                .chatLanguageModel(model)
                .systemMessageProvider(chatMemoryId -> prompt);
    }
}
