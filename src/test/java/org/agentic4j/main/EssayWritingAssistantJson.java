package org.agentic4j.main;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.agentic4j.json.JsonAgentGraphBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EssayWritingAssistantJson {

    private static final Logger log = LoggerFactory.getLogger(EssayWritingAssistantJson.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        EssayWritingAssistantJson test = new EssayWritingAssistantJson();
        test.canWriteEssay();
    }

    public void canWriteEssay() throws IOException {

        OpenAiChatModel model = OpenAiChatModel.builder()
                .modelName("gpt-4o-mini")
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();

        ChatMemory memory = MessageWindowChatMemory.builder()
                .chatMemoryStore(new InMemoryChatMemoryStore())
                .maxMessages(5)
                .build();


        Path path = Path.of("src/test/resources/graph.json");
        String json = Files.readString(path);
        AgenticWorkflow workflow = new JsonAgentGraphBuilder(json, model, memory).getWorkflow();

        workflow.start("Write a 500 word essay on Fyodor Dostoyevsky");
        log.info(workflow.fetchFinalOutput());
    }
}
