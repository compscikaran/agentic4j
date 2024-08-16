package org.agentic4j.main;

import org.agentic4j.api.Channel;
import org.agentic4j.api.Gatekeeper;
import org.agentic4j.api.Message;
import org.agentic4j.utils.*;
import org.flux.store.main.DuxStore;
import org.flux.store.main.DuxStoreBuilder;
import org.flux.store.utils.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Predicate;

public class AgenticWorkflow {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgenticWorkflow.class);

    private final AgenticGraph graph;
    private DuxStore<Channel> store;
    private Predicate<Message> predicate;
    private String terminalAgent;
    private Boolean isInitialized = false;
    private Gatekeeper gatekeeper;
    private Boolean asyncMode = false;
    private Integer maxMessages;

    public AgenticWorkflow(AgenticGraph graph, Predicate<Message> predicate, String terminalAgent, Gatekeeper gatekeeper, Boolean asyncMode, Integer maxMessages) {
        this.graph = graph;
        this.predicate = predicate;
        this.terminalAgent = terminalAgent;
        this.gatekeeper = gatekeeper;
        this.asyncMode = asyncMode;
        this.maxMessages = maxMessages;
    }

    public void init() {
        LOGGER.debug("##### Checking input parameters #####");

        if(!this.graph.getAgentGraph().keySet().contains(this.terminalAgent)) {
            throw new IllegalArgumentException("Terminal Agent not found in AgenticGraph");
        }

        LOGGER.debug("##### Setting up Dux backend #####");
        DuxStoreBuilder<Channel> builder = new DuxStoreBuilder<Channel>();
        builder.setInitialState(new Channel());
        LOGGER.debug("##### Setting up Dux Reducer ######");
        builder.setReducer(ReducerUtil.getChannelReducer(maxMessages)).build();
        LOGGER.debug("##### Setting up Circuit breaker ######");
        builder.setMiddleware(CircuitBreakerUtil.createCircuitBreaker(predicate, store));
        if(this.asyncMode)
            builder.enableAsyncNotifications();
        this.store = builder.build();
        LOGGER.debug("##### Setting up logging of messages #####");
        setupLogger();
        LOGGER.debug("##### Setting up subscriber functions based on AgentGraph #####");
        setupCommunicationUsingSubscribers();
        this.isInitialized = true;
    }

    private void setupCommunicationUsingSubscribers() {
        for(String agent: this.graph.getAgentGraph().keySet()) {
            List<String> listeners = this.graph.getAgentGraph().get(agent);
            for(String listener: listeners) {
                LOGGER.debug(String.format("Setting up subscriber: %s  -> %s", agent, listener));
                this.store.subscribe(SubscriberUtil.createSubscriber(agent, listener, graph, store));
            }
        }
    }

    private void setupLogger() {
        store.subscribe((state) -> {
            if(state.getUserMessages().size() > 0) {
                Message data = state.getUserMessages().getLast();
                LOGGER.info(String.format("%s:\n%s\n", data.sender(), data.message()));
            }
        });
    }

    public void start(String chat) {
        if(!this.isInitialized) {
            this.init();
        }
        if(this.gatekeeper.chat(chat)) {
            LOGGER.info("###### Passed Gatekeeper check... ######");
            dispatchChat(chat);
        } else {
            LOGGER.info("###### Failed Gatekeeper check... ######");
            this.endLoop();
        }
    }

    private void dispatchChat(String chat) {
        Message input = new Message(Constants.USER, chat);
        LOGGER.info("###### Workflow is starting... ######");
        this.store.dispatch(Utilities.actionCreator(Constants.ADD_MESSAGE, input));
    }

    public void endLoop() {
        this.store.dispatch(Utilities.actionCreator(Constants.STOP_LOOP, null));
    }

    public StopWorkflowTool getEndTool() {
        return new StopWorkflowTool(() -> this.endLoop());
    }

    public String fetchFinalOutput() {
        if(asyncMode) {
            waitForWorkflowCompletion();
        }
        LOGGER.info("###### Fetching final result of Workflow: #######");
        List<Message> allMessages = this.store.getState().getUserMessages();
        for (int i = allMessages.size() - 1; i > 0; i--) {
            Message currentMessage = allMessages.get(i);
            if(currentMessage.sender().equalsIgnoreCase(this.terminalAgent)) {
                return currentMessage.message();
            }
        }
        return "";
    }

    private void waitForWorkflowCompletion() {
        while (!this.store.getState().getStopLoop()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
