package xyz.lavoute.web.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import xyz.lavoute.web.dto.CredentialDTO;

@Service
@RequiredArgsConstructor
public class UserAuthenticationService {
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public Authentication authenticateUser(CredentialDTO credential) {
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(credential.username(), credential.password());

        return authenticationManager.authenticate(authRequest);
    }
}
