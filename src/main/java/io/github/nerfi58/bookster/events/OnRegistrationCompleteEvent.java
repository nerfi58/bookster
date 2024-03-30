package io.github.nerfi58.bookster.events;

import lombok.Getter;

@Getter
public class OnRegistrationCompleteEvent {

    private final long userId;

    public OnRegistrationCompleteEvent(long userId) {
        this.userId = userId;
    }
}
