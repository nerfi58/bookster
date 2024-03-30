package io.github.nerfi58.bookster.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendSimpleMessage(String to, String username, String token) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("booksterservice@gmail.com");
        message.setTo(to);
        message.setSubject("BOOKSTER - ACCOUNT VERIFICATION");

        String messageText = """
                Hello, %s. Welcome in Bookster Service!
                                
                In order to confirm your account, please follow this link:
                http://localhost:8080/activate?token=%s
                """.formatted(username, token);

        message.setText(messageText);

        javaMailSender.send(message);
    }
}
