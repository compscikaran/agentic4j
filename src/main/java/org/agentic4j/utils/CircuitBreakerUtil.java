package org.agentic4j.utils;

import org.agentic4j.api.Channel;
import org.agentic4j.api.Message;
import org.flux.store.api.Middleware;
import org.flux.store.main.DuxStore;
import org.flux.store.utils.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class CircuitBreakerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerUtil.class);

    public static Middleware<Channel> createCircuitBreaker(Predicate<Message> logic, DuxStore<Channel> duxStore) {
        return (store, next, action) -> {
            Message message = (Message) action.getPayload();
            if(logic.test(message)) {
                LOGGER.info("##### Circuit Breaker is being triggered #####");
                duxStore.dispatch(Utilities.actionCreator(Constants.STOP_LOOP, null));
            } else {
                next.accept(action);
            }
        };
    }
}
