package xyz.lavoute.web.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.lavoute.web.dto.Credential;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @PostMapping("/login")
    public String login(@RequestBody Credential credential) {
        return "Hello Authentication ! " + credential.toString();
    }
}
