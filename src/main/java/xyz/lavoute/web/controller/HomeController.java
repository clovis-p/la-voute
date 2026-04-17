package xyz.lavoute.web.controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class HomeController {

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
}
