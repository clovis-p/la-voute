package xyz.lavoute.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // Activer la protection csrf en prod
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/register",
                                "/login"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(login -> login
                        .successHandler((req, res, auth) -> res.setStatus(200))
                        .failureHandler((req, res, auth) -> res.setStatus(401))
                )
                .logout(logout -> logout
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(200))
                );
        return http.build();
    }
}
