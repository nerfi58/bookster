package io.github.nerfi58.bookster.test.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nerfi58.bookster.dtos.UserDto;
import io.github.nerfi58.bookster.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

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
    void givenUserDto_whenRegisteringUserAndUserWithThatUsernameAlreadyExists_thenRedirectToErrorPage() throws Exception {
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

        mockMvc.perform(post("/user/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(givenUserDto)))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?usernameAlreadyExists"));
    }

    @Test
    void givenUserDto_whenRegisteringUserAndUserWithGivenMailAlreadyExists_thenRedirectToErrorPage() throws Exception {
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

        mockMvc.perform(post("/user/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(givenUserDto)))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?emailAlreadyExists"));
    }
}

