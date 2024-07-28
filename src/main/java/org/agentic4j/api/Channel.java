package org.agentic4j.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.flux.store.api.State;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Channel implements State {

    private List<Message> userMessages = new ArrayList<>();
    private Boolean stopLoop = false;



    @Override
    public State clone() {
        try {
            Channel newObj = (Channel) super.clone();
            newObj.userMessages = new ArrayList<>();
            newObj.userMessages.addAll(this.userMessages);
            return newObj;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
