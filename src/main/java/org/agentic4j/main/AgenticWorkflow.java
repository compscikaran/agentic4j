package org.agentic4j.main;

import org.agentic4j.api.Channel;
import org.agentic4j.api.Message;
import org.flux.store.api.Action;
import org.flux.store.api.Middleware;
import org.flux.store.api.Reducer;
import org.flux.store.main.DuxStore;
import org.flux.store.main.DuxStoreBuilder;
import org.flux.store.utils.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AgenticWorkflow {

    private static final Logger log = LoggerFactory.getLogger(AgenticWorkflow.class);


    public static final String ADD_MESSAGE = "addMessage";
    public static final String STOP_LOOP = "stopLoop";
    public static final String USER = "User";

    private final AgenticGraph graph;
    private DuxStore<Channel> store;

    public AgenticWorkflow(AgenticGraph graph) {
        this.graph = graph;
    }

    public void init(Optional<Predicate<Message>> circuitBreaker) {
        log.debug("##### Setting up Dux backend #####");
        DuxStoreBuilder<Channel> builder = new DuxStoreBuilder<Channel>();
        builder.setInitialState(new Channel());
        log.debug("##### Setting up Dux Reducer ######");
        builder.setReducer(getChannelReducer()).build();
        log.debug("##### Setting up Circuit breaker ######");
        if(circuitBreaker.isPresent())
            builder.setMiddleware(createCircuitBreaker(circuitBreaker.get()));
        this.store = builder.build();
        log.debug("##### Setting up logging of messages #####");
        setupLogger();
        log.debug("##### Setting up subscriber functions based on AgentGraph #####");
        setupSubscribersFromGraph();
    }

    private void setupSubscribersFromGraph() {
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
            log.info(String.format("%s:\n%s\n", data.sender(),data.message()));
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

    private  Middleware<Channel> createCircuitBreaker(Predicate<Message> logic) {
        return (store, next, action) -> {
            Message message = (Message) action.getPayload();
            if(logic.test(message)) {
                this.endLoop();
                Action<String> modifiedAction = Utilities.actionCreator(action.getType(), "");
                next.accept(modifiedAction);
            }
            next.accept(action);
        };
    }

    public void addUserMessage(String chat) {
        Message input = new Message(USER, chat);
        log.info("###### Workflow is starting... ######");
        this.store.dispatch(Utilities.actionCreator(ADD_MESSAGE, input));
    }

    public void endLoop() {
        this.store.dispatch(Utilities.actionCreator(STOP_LOOP, null));
    }

    public String fetchFinalOutput(String agentName) {
        log.info("###### Fetching final result of Workflow: #######");
        List<Message> allMessages = this.store.getState().getUserMessages();
        for (int i = allMessages.size() - 1; i > 0; i--) {
            Message currentMessage = allMessages.get(i);
            if(currentMessage.sender().equalsIgnoreCase(agentName)) {
                return currentMessage.message();
            }
        }
        return "";
    }
}
