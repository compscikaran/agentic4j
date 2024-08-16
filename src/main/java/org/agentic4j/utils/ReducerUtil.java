package org.agentic4j.utils;

import org.agentic4j.api.Channel;
import org.agentic4j.api.Message;
import org.flux.store.api.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReducerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReducerUtil.class);

    public static Reducer<Channel> getChannelReducer() {
        return (action, state) -> {
            if(action.getType().equalsIgnoreCase(Constants.ADD_MESSAGE)) {
                Message message = (Message) action.getPayload();
                state.getUserMessages().add(message);
            } else if(action.getType().equalsIgnoreCase(Constants.STOP_LOOP)) {
                state.setStopLoop(true);
            }
            return state;
        };
    }
}
