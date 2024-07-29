package org.agentic4j.utils;

import org.agentic4j.api.Message;

import java.util.function.Predicate;

public class Utils {

    public static final Predicate<Message> NO_CIRCUIT_BREAKER = (message) -> true;
}
