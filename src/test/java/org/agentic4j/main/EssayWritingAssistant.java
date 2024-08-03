package org.agentic4j.main;

import org.agentic4j.api.Agent;
import org.agentic4j.api.Gatekeeper;
import org.agentic4j.main.utils.AgentFactory;
import org.agentic4j.main.utils.Prompts;
import org.agentic4j.utils.StopWorkflowTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EssayWritingAssistant {

    private static final Logger log = LoggerFactory.getLogger(EssayWritingAssistant.class);
    public static final String WRITER_AGENT = "Writer";
    public static final String CRITIC_AGENT = "Critic";
    public static final String GATEKEEPER = "Gatekeeper";

    public static void main(String[] args) throws InterruptedException {
        EssayWritingAssistant test = new EssayWritingAssistant();
        test.canWriteEssay();
    }

    public void canWriteEssay() {
        Gatekeeper gatekeeper = AgentFactory.createGatekeeper(Prompts.GATEKEEPER, "gpt-4o-mini")
                .build();

        AgenticGraph graph = new AgenticGraph();
        AgenticWorkflow workflow = new AgenticWorkflowBuilder()
                .setGraph(graph)
                .setTerminalAgent(WRITER_AGENT)
                .setGatekeeper(gatekeeper)
                .build();

        Agent writer = AgentFactory.createAgent(Prompts.WRITER, "gpt-4o-mini")
                .build();
        Agent critic = AgentFactory.createAgent(Prompts.CRITIC, "gpt-4o-mini")
                .tools(new StopWorkflowTool(workflow::endLoop))
                .build();

        graph.addAgent(WRITER_AGENT, writer, List.of(CRITIC_AGENT));
        graph.addAgent(CRITIC_AGENT, critic, List.of(WRITER_AGENT));
        graph.addAgent(AgenticWorkflow.USER, null, List.of(WRITER_AGENT));

        workflow.addUserMessage("Write a 500 word essay on Fyodor Dostoyevsky");
        log.info(workflow.fetchFinalOutput());
    }

}
