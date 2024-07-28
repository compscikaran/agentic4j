package org.agentic4j.main;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.agentic4j.api.Agent;
import org.agentic4j.main.utils.AgentFactory;
import org.agentic4j.main.utils.Prompts;
import org.agentic4j.utils.StopWorkflowTool;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class EssayWritingTest {

    public static final String WRITER_AGENT = "Writer";
    public static final String CRITIC_AGENT = "Critic";

    @Test
    public void canWriteEssay() {


        AgenticGraph graph = new AgenticGraph();
        AgenticWorkflow workflow = new AgenticWorkflow(graph);
        Runnable stopSignal = workflow::endLoop;

        ChatMemory memory = MessageWindowChatMemory.builder().chatMemoryStore(new InMemoryChatMemoryStore()).maxMessages(5).build();
        ChatMemory memory2 = MessageWindowChatMemory.builder().chatMemoryStore(new InMemoryChatMemoryStore()).maxMessages(5).build();

        Agent writer = AgentFactory.createAgent(WRITER_AGENT, Prompts.WRITER, "gpt-4o-mini")
                .chatMemory(memory)
                .build();
        Agent critic = AgentFactory.createAgent(CRITIC_AGENT, Prompts.CRITIC, "gpt-4o").
                chatMemory(memory2)
                .tools(new StopWorkflowTool(stopSignal))
                .build();

        graph.addAgent(WRITER_AGENT, writer, List.of(CRITIC_AGENT));
        graph.addAgent(CRITIC_AGENT, critic, List.of(WRITER_AGENT));
        graph.addAgent(AgenticWorkflow.USER, null, List.of(WRITER_AGENT));
        workflow.init();

        workflow.addUserMessage("Write a 500 word essay on Fyodor Dostoyevsky");
    }

}