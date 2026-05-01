package xyz.lavoute.web.integrations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LogoutTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenLogoutRequest_whenHasSessionIdAndCsrf_thenReturn200OK() throws Exception {
        mockMvc.perform(
                post("/logout")
                        .with(user("authenticatedUser"))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(unauthenticated());
    }

    @Test
    // Server returns 200 so that hacker cannot guess server authentication state
    void givenLogoutRequest_whenAlreadyDisconnected_thenHidesServerStatusWith200OK() throws Exception {
        mockMvc.perform(
                        post("/logout")
                                .with(anonymous())
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(unauthenticated());
    }

    @Test
    void givenLogoutRequest_whenMissingCsrf_then403Unauthorized() throws Exception {
        mockMvc.perform(
                        post("/logout")
                                .with(user("authenticatedUser"))
                )
                .andExpect(status().isForbidden());
    }
}
