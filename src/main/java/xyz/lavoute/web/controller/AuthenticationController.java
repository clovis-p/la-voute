package xyz.lavoute.web.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.lavoute.web.dto.CredentialDTO;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @PostMapping("/login")
    public String login(@RequestBody CredentialDTO credentialDTO) {
        return "Hello Authentication ! " + credentialDTO.toString();
    }
}
