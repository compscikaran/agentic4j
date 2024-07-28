package org.agentic4j.main;

import org.agentic4j.api.Channel;
import org.agentic4j.api.Message;
import org.flux.store.api.Reducer;
import org.flux.store.main.DuxStore;
import org.flux.store.main.DuxStoreBuilder;
import org.flux.store.utils.Utilities;

import java.util.List;
import java.util.function.Consumer;

public class AgenticWorkflow {

    public static final String ADD_MESSAGE = "addMessage";
    public static final String STOP_LOOP = "stopLoop";
    public static final String USER = "User";

    private final AgenticGraph graph;
    private DuxStore<Channel> store;

    public AgenticWorkflow(AgenticGraph graph) {
        this.graph = graph;
    }

    public void init() {
        Reducer<Channel> reducer = getChannelReducer();
        this.store = new DuxStoreBuilder<Channel>()
                .setInitialState(new Channel())
                .setReducer(reducer).build();
        setupLogger();
        setupListenersFromGraph();
    }

    private void setupListenersFromGraph() {
        for(String agent: this.graph.getAgentGraph().keySet()) {
            Consumer<Channel> subscriber = (state) -> {
                if(state.getStopLoop())
                    return;
                List<String> listeners = this.graph.getAgentGraph().get(agent);
                Message lastMessage = state.getUserMessages().getLast();
                if(lastMessage.sender().equalsIgnoreCase(agent)) {
                    for(String listener: listeners) {
                        String response = this.graph.chatToAgent(listener, relayMessage(lastMessage));
                        this.store.dispatch(Utilities.actionCreator(ADD_MESSAGE, new Message(listener, response)));
                    }
                }
            };
            store.subscribe(subscriber);
        }
    }

    private static String relayMessage(Message lastMessage) {
        return lastMessage.sender() + "says :" + lastMessage.message();
    }

    private void setupLogger() {
        store.subscribe((state) -> {
            Message data = state.getUserMessages().getLast();
            System.out.println(data.sender() + ": \n" + data.message() + "\n");
        });
    }

    private static Reducer<Channel> getChannelReducer() {
        Reducer<Channel> reducer = (action, state) -> {
            if(action.getType().equalsIgnoreCase(ADD_MESSAGE)) {
                Message message = (Message) action.getPayload();
                state.getUserMessages().add(message);
            } else if(action.getType().equalsIgnoreCase(STOP_LOOP)) {
                state.setStopLoop(true);
            }
            return state;
        };
        return reducer;
    }

    public void addUserMessage(String chat) {
        Message input = new Message(USER, chat);
        System.out.println("###### Workflow is starting... ######");
        this.store.dispatch(Utilities.actionCreator(ADD_MESSAGE, input));
    }

    public void endLoop() {
        this.store.dispatch(Utilities.actionCreator(STOP_LOOP, null));
    }
}
