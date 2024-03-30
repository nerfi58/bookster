package io.github.nerfi58.bookster.events.listeners;

import io.github.nerfi58.bookster.events.OnTokenGenerationCompleteEvent;
import io.github.nerfi58.bookster.services.MailService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ConfirmationTokenListener {

    private final MailService mailService;

    public ConfirmationTokenListener(MailService mailService) {
        this.mailService = mailService;
    }

    @EventListener
    public void eventListener(OnTokenGenerationCompleteEvent event) {
        mailService.sendSimpleMessage(event.getUser().getEmail(), event.getUser().getUsername(), event.getToken());
    }
}
