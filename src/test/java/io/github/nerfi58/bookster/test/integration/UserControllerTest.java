package io.github.nerfi58.bookster.test.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import io.github.nerfi58.bookster.config.SecurityTestConfig;
import io.github.nerfi58.bookster.dtos.UserDto;
import io.github.nerfi58.bookster.entities.ConfirmationToken;
import io.github.nerfi58.bookster.entities.User;
import io.github.nerfi58.bookster.repositories.ConfirmationTokenRepository;
import io.github.nerfi58.bookster.repositories.UserRepository;
import io.github.nerfi58.bookster.services.UserService;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(classes = SecurityTestConfig.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    private static GreenMail smtpServer;

    @BeforeAll
    static void beforeAll() {
        smtpServer = new GreenMail(new ServerSetup(2525, null, ServerSetup.PROTOCOL_SMTP));
        smtpServer.setUser("username", "secret");
        smtpServer.start();
    }

    @BeforeEach
    void beforeEach() throws FolderException {
        smtpServer.purgeEmailFromAllMailboxes();
    }

    @AfterAll
    static void afterAll() {
        smtpServer.stop();
    }

    @Test
    void givenUserDto_whenRegisteringUserAndAllDataIsCorrect_thenReturnLocationAndHxRedirectHeadersWithRegisteredUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .username("testUser")
                .rawPassword("password1")
                .email("testEmail@example.com")
                .build();

        MvcResult result = mockMvc.perform(post("/user/register")
                                                   .with(csrf())
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content(objectMapper.writeValueAsString(userDto))).andReturn();

        UserDto savedUser = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(result.getResponse().getHeader("HX-Redirect")).isEqualTo("/login?registerSuccessful");
        assertThat(result.getResponse().getHeader("location")).isEqualTo("/user/" + savedUser.getId());
    }

    @Test
    void givenUserDto_whenRegisteringUserAndUserWithThatUsernameAlreadyExists_thenReturnRedirectHeaderWithErrorUrl() throws Exception {
        UserDto existingUser = UserDto.builder()
                .username("usernameWhichExists")
                .rawPassword("password")
                .email("testEmail1@example.com")
                .build();

        userService.registerUser(existingUser);

        UserDto givenUserDto = UserDto.builder()
                .username("usernameWhichExists")
                .rawPassword("password")
                .email("anotherEmail@example")
                .build();

        MvcResult result = mockMvc.perform(post("/user/register")
                                                   .with(csrf())
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content(objectMapper.writeValueAsString(givenUserDto))).andReturn();

        assertThat(result.getResponse().getHeader("HX-Redirect")).isEqualTo("/register?usernameAlreadyExists");
    }

    @Test
    void givenUserDto_whenRegisteringUserAndUserWithGivenMailAlreadyExists_thenReturnRedirectHeaderWithErrorUrl() throws Exception {
        UserDto existingUser = UserDto.builder()
                .username("sampleUser")
                .rawPassword("password")
                .email("existingEmail@example.com")
                .build();

        userService.registerUser(existingUser);

        UserDto givenUserDto = UserDto.builder()
                .username("testUser")
                .rawPassword("password")
                .email("existingEmail@example.com")
                .build();

        MvcResult result = mockMvc.perform(post("/user/register")
                                                   .with(csrf())
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content(objectMapper.writeValueAsString(givenUserDto))).andReturn();

        assertThat(result.getResponse().getHeader("HX-Redirect")).isEqualTo("/register?emailAlreadyExists");
    }

    @Test
    @Transactional
    void givenUserUd_whenGenerateToken_thenGenerateTokenForUserWithGivenIdAndSendMailToThatUser() throws Exception {
        UserDto user = UserDto.builder()
                .username("testUser")
                .rawPassword("password")
                .email("mail@example.com")
                .build();

        userService.registerUser(user);

        MvcResult result = mockMvc.perform(post("/user/generate-token?u=4").with(csrf())).andReturn();

        User savedUser = userRepository.findById(4L).orElseThrow(EntityNotFoundException::new);

        ConfirmationToken token = confirmationTokenRepository.findByUser(savedUser)
                .orElseThrow(EntityNotFoundException::new)
                .getFirst();

        MimeMessage message = smtpServer.getReceivedMessages()[0];

        String expectedMessage = """
                Hello, testuser. Welcome in Bookster Service!
                                
                In order to confirm your account, please follow this link:
                http://localhost:8080/activate?token=%s
                """.formatted(token.getToken());

        assertThat(message.getSubject()).isEqualTo("BOOKSTER - ACCOUNT VERIFICATION");
        assertThat(message.getFrom()[0].toString()).isEqualTo("booksterservice@gmail.com");
        assertThat(message.getContent().toString()).isEqualToIgnoringNewLines(expectedMessage);
        assertThat(token.getToken()).isNotNull();
        assertThat(token.getUser().getUsername()).isEqualTo("testuser");
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(result.getResponse().getHeader("HX-Redirect")).isEqualTo("/login?registerSuccessful");

    }

    @Test
    void givenUserDto_whenActivateUserByToken_thenActivateUserAssociatedWithThatTokenAndRedirect() throws Exception {
        UserDto notActiveUser = UserDto.builder()
                .username("notActiveUser")
                .rawPassword("testPassword")
                .email("notActiveUser@example.com")
                .build();

        userService.registerUser(notActiveUser);

        User user = userRepository.findUserByUsername("notActiveUser".toLowerCase())
                .orElseThrow(EntityNotFoundException::new);

        List<ConfirmationToken> confirmationTokenList = confirmationTokenRepository.findByUser(user)
                .orElseThrow(EntityNotFoundException::new);

        ConfirmationToken confirmationToken = confirmationTokenList.getFirst();

        MvcResult result = mockMvc.perform(post("/user/activate?token=" + confirmationToken.getToken())
                                                   .with(csrf())).andReturn();

        User userAfterActivating = userRepository.findUserByUsername("notActiveUser".toLowerCase())
                .orElseThrow(EntityNotFoundException::new);

        assertThat(result.getResponse().getHeader("HX-Redirect")).isEqualTo("/login?activationSuccessful");
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(userAfterActivating.isActive()).isTrue();
    }
}

