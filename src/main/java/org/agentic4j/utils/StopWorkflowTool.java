package org.agentic4j.utils;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopWorkflowTool {

    private static final Logger log = LoggerFactory.getLogger(StopWorkflowTool.class);

    private final Runnable callback;

    public StopWorkflowTool(Runnable callback) {
        this.callback = callback;
    }

    @Tool("Stops the workflow")
    public void stopTool() {
        log.info("###### Workflow is done... ######");
        callback.run();
    }
}
