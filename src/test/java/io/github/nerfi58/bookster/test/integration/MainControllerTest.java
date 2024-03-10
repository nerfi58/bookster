package io.github.nerfi58.bookster.test.integration;

import io.github.nerfi58.bookster.config.SecurityTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SecurityTestConfig.class)
@AutoConfigureMockMvc
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenActiveUser_whenLoginIn_thenRedirectToMainPage() throws Exception {
        mockMvc.perform(formLogin().user("userActive").password("password")).andExpect(redirectedUrl("/"));
    }

    @Test
    void givenNotActiveUser_whenLoginIn_thenRedirectWithNotActivatedParameter() throws Exception {
        mockMvc.perform(formLogin().user("userNotActive").password("password"))
                .andExpect(redirectedUrl("/login?notActivated"));
    }

    @Test
    void givenBadCredentials_whenLoginIn_thenRedirectWithBadCredentialsParameter() throws Exception {
        mockMvc.perform(formLogin().user("thisUserDoesNotExist").password("password"))
                .andExpect(redirectedUrl("/login?badCredentials"));
    }

    @Test
    void givenNoAuthentication_whenEnteringLoginPage_thenReturnOk() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8)))
                .andExpect(status().isOk());
    }

    @Test
    void givenNoAuthentication_whenEnteringMainPage_thenRedirectToLoginPage() throws Exception {
        MvcResult result = mockMvc.perform(get("/")).andReturn();
        assertThat(result.getResponse().getRedirectedUrl()).matches("https?://[A-Za-z0-9]*(:[0-9]{1,5})?/login");
    }

    @Test
    @WithUserDetails("userActive")
    void givenAuthentication_whenEnteringLoginPage_thenRedirectToMainPage() throws Exception {
        mockMvc.perform(get("/login")).andExpect(redirectedUrl("/"));
    }

    @Test
    @WithUserDetails("userActive")
    void givenAuthentication_whenEnteringRegisterPage_thenRedirectToMainPage() throws Exception {
        mockMvc.perform(get("/register")).andExpect(redirectedUrl("/"));
    }
}
