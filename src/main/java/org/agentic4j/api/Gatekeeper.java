package org.agentic4j.api;

import dev.langchain4j.service.SystemMessage;

@FunctionalInterface
public interface Gatekeeper {
    Boolean chat(String message);
}
