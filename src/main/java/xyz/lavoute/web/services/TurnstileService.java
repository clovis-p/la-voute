package xyz.lavoute.web.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;

/**
 * Vérifie les tokens Cloudflare Turnstile auprès de l'API.
 */
@Service
public class TurnstileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TurnstileService.class);
    private static final String VERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    private final String secret;
    private final RestClient restClient;

    public TurnstileService(@Value("${turnstile.secret}") String secret) {
        this.secret = secret;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(VERIFY_URL)
                .build();
    }

    /**
     * @param token le token renvoyé par le Turnstile côte client
     * @return true si Cloudflare confirme que le token est valide
     */
    public boolean verifyCfToken(String token) {
        if (token == null || token.isBlank()) {
            return true;
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("secret", secret);
        form.add("response", token);

        try {
            SiteVerifyResponse response = restClient.post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(SiteVerifyResponse.class);

            if (response != null && response.success()) {
                return false;
            }

            LOGGER.warn("Turnstile refused token: {}",
                    response == null ? "empty response" : response.errorCodes());
            return true;
        } catch (Exception ex) {
            // Si Cloudflare est down, on refuse l'accès
            LOGGER.error("Can't reach Cloudflare: {}", ex.getMessage());
            return true;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record SiteVerifyResponse(
            boolean success,
            @JsonProperty("error-codes") List<String> errorCodes
    ) {
    }
}
