package org.agentic4j.main;

import org.agentic4j.api.Channel;
import org.agentic4j.api.Message;
import org.agentic4j.utils.Constants;
import org.flux.store.api.InvalidActionException;
import org.flux.store.main.DuxSlice;
import org.flux.store.main.DuxSliceBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AgenticWorkflow {

    public static final String ADD_MESSAGE = "addMessage";

    private final AgenticGraph graph;
    private final DuxSlice<Channel> slice;

    public AgenticWorkflow(AgenticGraph graph) {
        this.graph = graph;
        DuxSliceBuilder<Channel> sliceBuilder = new DuxSliceBuilder<Channel>()
                .setInitialState(new Channel(new ArrayList<>()))
                .addReducer(ADD_MESSAGE, ((action, state) -> {
                    Message message = (Message) action.getPayload();
                    state.getUserMessages().add(message);
                    return state;
                }))
                .addSubscriber(System.out::println);

        for(String agent: this.graph.getAgentGraph().keySet()) {
            Consumer<Channel> subscriber = (state) -> {
                List<String> listeners = this.graph.getAgentGraph().get(agent);
                Message lastMessage = state.getUserMessages().getLast();
                if(lastMessage.sender().equalsIgnoreCase(agent)) {
                    for(String listener: listeners) {
                        this.graph.chatToAgent(listener, lastMessage.message());
                    }
                }
            };
            sliceBuilder.addSubscriber(subscriber);
        }
        this.slice = sliceBuilder.build();
    }

    public void addUserMessage(String chat) throws InvalidActionException {
        Message input = new Message(Constants.USER, chat);
        this.slice.getAction(ADD_MESSAGE).accept(input);
    }
}
