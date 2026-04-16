package xyz.lavoute.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.lavoute.web.dto.CredentialDTO;
import xyz.lavoute.web.services.UserAuthenticationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserAuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody CredentialDTO credentialDTO) {
        Authentication authentication = authenticationService.authenticateUser(credentialDTO);

        if (!authentication.isAuthenticated()) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // TODO temporary
        return ResponseEntity.ok().build();
    }
}
