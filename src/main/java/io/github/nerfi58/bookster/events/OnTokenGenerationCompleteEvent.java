package io.github.nerfi58.bookster.events;

import io.github.nerfi58.bookster.entities.User;
import lombok.Getter;

@Getter
public class OnTokenGenerationCompleteEvent {

    private User user;
    private String token;

    public OnTokenGenerationCompleteEvent(User user, String token) {
        this.user = user;
        this.token = token;
    }
}
