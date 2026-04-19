package xyz.lavoute.web.integrations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.UserRepository;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        // DB user
        User user = new User();
        String username = "Hello";
        String password = "Spring";
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Test
    void givenCSRFRequest_whenReceived_thenReturnInHeaderXSRF_TOKEN() throws Exception {
        mockMvc
                .perform(get("/api/csrf"))
                .andExpect(status().isOk())
                .andExpect(request().attribute("_csrf", notNullValue()));
    }

    @Test
    void givenLoginRequest_whenInvalidUsername_then401Unauthorized() throws Exception {
        mockMvc
                .perform(
                        formLogin("/login")
                                .user("InvalidUsername")
                                .password("Spring")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenLoginRequest_whenInvalidPassword_then401Unauthorized() throws Exception {
        mockMvc
                .perform(
                        formLogin("/login")
                                .user("Hello")
                                .password("Summer")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenLoginRequest_whenMissingCSRFToken_then401Unauthorized() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "Hello")
                        .param("password", "Spring")
                        // Excluded csrf token
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenLoginRequest_whenValidCredentialsWithCsrfToken_then200OKWithJSESSIONIDInHeader() throws Exception {
        mockMvc
                .perform(
                        formLogin("/login")
                                .user("Hello")
                                .password("Spring")
                )
                .andExpect(status().isOk())
                .andExpect(request().sessionAttribute("SPRING_SECURITY_CONTEXT", notNullValue()));
    }
}
