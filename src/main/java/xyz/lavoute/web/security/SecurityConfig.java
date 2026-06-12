package xyz.lavoute.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import xyz.lavoute.web.services.TurnstileService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TurnstileService turnstileService) {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/home",
                                "/api/csrf",
                                "/api/user/register",
                                "/api/files/{fileId}/download"
                        )
                        .permitAll()
                        .requestMatchers("/api/files/**").hasRole("USER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(login -> login
                        .successHandler((req, res, auth) -> res.setStatus(200))
                        .failureHandler((req, res, auth) -> res.setStatus(401))
                )
                .logout(logout -> logout
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(200))
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                )
                .addFilterBefore(
                        new TurnstileLoginFilter(turnstileService),
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }
}
