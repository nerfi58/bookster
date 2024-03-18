package io.github.nerfi58.bookster.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/activate")
    public String activate(Model model, @RequestParam(name = "token") String token) {
        model.addAttribute("token", token);
        return "activate";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
