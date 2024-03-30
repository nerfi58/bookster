package io.github.nerfi58.bookster.events.listeners;

import io.github.nerfi58.bookster.events.OnRegistrationCompleteEvent;
import io.github.nerfi58.bookster.services.UserConfirmationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener {

    private UserConfirmationService userConfirmationService;

    public RegistrationListener(UserConfirmationService userConfirmationService) {
        this.userConfirmationService = userConfirmationService;
    }

    @EventListener
    public void eventListener(OnRegistrationCompleteEvent event) {
        userConfirmationService.generateConfirmationToken(event.getUserId());
    }
}
