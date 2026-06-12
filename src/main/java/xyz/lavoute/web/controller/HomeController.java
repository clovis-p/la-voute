package xyz.lavoute.web.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class HomeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    private static final List<String> SUBTITLES = List.of(
            "Votre IRL Ender Chest",
            "Rangez. Retrouvez. Respirez.",
            "Le désordre, c'est fini.",
            "Tout à sa place, toujours.",
            "Votre mémoire physique."
    );

    private final Random random = new Random();

    @GetMapping("/home")
    public Map<String, String> home() {
        String subtitle = SUBTITLES.get(random.nextInt(SUBTITLES.size()));
        return Map.of("titleMessage", "Bienvenue dans La Voûte!", "subtitleMessage", subtitle);
    }

    @GetMapping("/csrf")
    public void getCsrfToken(CsrfToken token) {
        LOGGER.info("csrf token requested");
        token.getToken();
    }
}
