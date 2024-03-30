package io.github.nerfi58.bookster.test.unit;

import io.github.nerfi58.bookster.services.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

@SpringBootTest
@ActiveProfiles("test")
public class MailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    private MailService mailService;

    @BeforeEach
    void setUp() {
        mailService = new MailService(javaMailSender);
    }

    @Test
    void givenUser_whenGenerateToken_thenSendEmailWithConfirmationTokenToUserEmail() {
        String usernameEmail = "testuser@example.com";
        String username = "testuser";
        String token = "288a9919-ccbb-40f3-abb1-617bcfe43acb";

        mailService.sendVerificationMessage(usernameEmail, username, token);

        ArgumentCaptor<SimpleMailMessage> mailMessageArgumentCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        then(javaMailSender).should().send(mailMessageArgumentCaptor.capture());

        String message = """
                Hello, testuser. Welcome in Bookster Service!
                                
                In order to confirm your account, please follow this link:
                http://localhost:8080/activate?token=288a9919-ccbb-40f3-abb1-617bcfe43acb
                """;

        assertThat(mailMessageArgumentCaptor.getValue().getSubject()).isEqualTo("BOOKSTER - ACCOUNT VERIFICATION");
        assertThat(mailMessageArgumentCaptor.getValue().getFrom()).isEqualTo("booksterservice@gmail.com");
        assertThat(mailMessageArgumentCaptor.getValue().getText()).isEqualTo(message);

    }
}
