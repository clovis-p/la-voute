package xyz.lavoute.web;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.UserRepository;

@SpringBootApplication
public class LaVouteApplication {
    public static void main(String[] args) {
        SpringApplication.run(LaVouteApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(UserRepository repository, PasswordEncoder encoder) {
        return args -> {
            User user = new User();
            user.setUsername("username");
            user.setPassword(encoder.encode("password"));

            repository.save(user);
        };
    }
}
