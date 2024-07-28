package org.agentic4j.utils;

import dev.langchain4j.agent.tool.Tool;

public class StopWorkflowTool {

    private Runnable callback;

    public StopWorkflowTool(Runnable callback) {
        this.callback = callback;
    }

    @Tool("Stops the workflow")
    public void stopTool() {
        System.out.println("###### Workflow is done... ######");
        callback.run();
    }
}
