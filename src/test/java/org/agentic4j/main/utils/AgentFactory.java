package org.agentic4j.main.utils;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.agentic4j.api.Agent;
import org.agentic4j.api.Gatekeeper;

public class AgentFactory {

    private static final String apiKey = System.getenv("OPENAI_API_KEY");

    public static AiServices<Agent> createAgent(String prompt, String modelName) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(apiKey)
                .build();
        ChatMemory memory = MessageWindowChatMemory.builder()
                .chatMemoryStore(new InMemoryChatMemoryStore())
                .maxMessages(5)
                .build();
        return AiServices.builder(Agent.class)
                .chatLanguageModel(model)
                .chatMemory(memory)
                .systemMessageProvider(chatMemoryId -> prompt);
    }

    public static AiServices<Gatekeeper> createGatekeeper(String prompt, String modelName) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(apiKey)
                .build();
        ChatMemory memory = MessageWindowChatMemory.builder()
                .chatMemoryStore(new InMemoryChatMemoryStore())
                .maxMessages(5)
                .build();
        return AiServices.builder(Gatekeeper.class)
                .chatLanguageModel(model)
                .chatMemory(memory)
                .systemMessageProvider(chatMemoryId -> prompt);
    }
}
