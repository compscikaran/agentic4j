package org.agentic4j.utils;

import org.agentic4j.api.Channel;
import org.agentic4j.api.Message;
import org.agentic4j.main.AgenticGraph;
import org.agentic4j.main.AgenticWorkflow;
import org.flux.store.main.DuxStore;
import org.flux.store.utils.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class SubscriberUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriberUtil.class);

    public static Consumer<Channel> createSubscriber(String agent, String listener, AgenticGraph graph, DuxStore<Channel> store) {
        return (state) -> {
            if (state.getStopLoop())
                return;
            LOGGER.debug("Running on thread: " + Thread.currentThread().threadId());
            Message lastMessage = state.getUserMessages().getLast();
            if (lastMessage.sender().equalsIgnoreCase(agent)) {
                String response = graph.chatToAgent(listener, relayMessage(lastMessage));
                store.dispatch(Utilities.actionCreator(Constants.ADD_MESSAGE, new Message(listener, response)));
            }
        };
    }

    private static String relayMessage(Message lastMessage) {
        return lastMessage.sender() + "says :" + lastMessage.message();
    }
}
