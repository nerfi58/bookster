package io.github.nerfi58.bookster.controllers;

import io.github.nerfi58.bookster.dtos.UserDto;
import io.github.nerfi58.bookster.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    private ResponseEntity<UserDto> register(@Valid @RequestBody UserDto userDto) {

        UserDto savedUser = userService.registerUser(userDto);

        UriComponents location = UriComponentsBuilder.fromPath("/user/{id}").buildAndExpand(savedUser.getId());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("HX-Redirect", "/login?registerSuccessful");

        return ResponseEntity.created(location.toUri()).headers(responseHeaders).body(savedUser);
    }

    @PostMapping("/activate")
    private ResponseEntity<Void> activate(@RequestParam(name = "token") String token) {

        userService.activateUserAccount(token);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("HX-Redirect", "/login?activationSuccessful");

        return ResponseEntity.noContent().headers(responseHeaders).build();
    }
}
