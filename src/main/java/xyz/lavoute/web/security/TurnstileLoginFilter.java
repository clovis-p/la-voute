package xyz.lavoute.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.lavoute.web.services.TurnstileService;

import java.io.IOException;

/**
 * Vérifie le token Turnstile avant de tenter l'authentification
 */
public class TurnstileLoginFilter extends OncePerRequestFilter {
    private final TurnstileService turnstileService;

    public TurnstileLoginFilter(TurnstileService turnstileService) {
        this.turnstileService = turnstileService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        boolean isLoginAttempt = "POST".equalsIgnoreCase(request.getMethod())
                && "/login".equals(request.getRequestURI());

        if (isLoginAttempt) {
            String token = request.getParameter("cf-turnstile-response");
            if (turnstileService.verifyCfToken(token)) {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
