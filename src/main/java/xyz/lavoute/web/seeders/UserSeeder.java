package xyz.lavoute.web.seeders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import xyz.lavoute.web.dto.RegistrationRequestDTO;
import xyz.lavoute.web.services.UserService;

import java.util.List;
import java.util.Random;

// Just remove the Component annotations to not run the seeder
@Component
@Profile("dev")
public class UserSeeder implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserSeeder.class);

    private static final int USER_AMOUNT = 3;

    private final List<String> names = List.of(
            "James", "Mary", "Michael", "Patricia", "John",
            "Jennifer", "Robert", "Linda", "David", "Elizabeth",
            "William", "Barbara", "Richard", "Susan", "Joseph",
            "Jessica", "Thomas", "Karen", "Christopher", "Sarah",
            "Charles", "Lisa", "Daniel", "Nancy", "Matthew",
            "Sandra", "Anthony", "Ashley", "Mark", "Emily",
            "Steven", "Kimberly", "Donald", "Betty", "Andrew",
            "Margaret", "Joshua", "Donna", "Paul", "Michelle",
            "Kenneth", "Carol", "Kevin", "Amanda", "Brian",
            "Melissa", "Timothy", "Deborah", "Ronald", "Stephanie"
    );

    private final List<String> usernames = List.of(
            "SnackAttacker", "Captain", "Obvious", "WiFi", "Whisperer", "Noodle", "Ninja",
            "Couch", "Commander", "Burrito", "Boss", "Penguin", "Patrol", "Pixel",
            "Pirate", "Taco", "Tornado", "Waffle", "Warrior", "Llama", "Drama",
            "Toast", "Master", "Cookie", "Monster", "Jr", "Banana", "Bandana",
            "Muffin", "Maniac", "Sleepy", "Sloth", "Thunder", "Pants", "Pickle",
            "Tickle", "Giggles", "McGee", "Snore", "Champion", "Pancake", "Pete",
            "Donut", "Deputy", "Pizza", "Prophet", "Nacho", "Normal", "Yawn",
            "Patrol", "Blanket", "Burglar", "Spud", "Stud", "Cheese", "Whiz",
            "Snack", "Snatcher", "Waddle", "Walker", "Bubble", "Trouble", "Nap",
            "Enthusiast", "Meme", "Lord", "Lazy", "Legend", "Burp", "Boss"
    );

    private final List<String> symbols = List.of("!", "]", "+", ")", "*", "=", "(", "}", "{", "[", "&", "$");

    private final Random random = new Random();
    private UserService service;

    public UserSeeder(UserService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("User Seeder running ...");
        LOGGER.info("Seeder will create " + USER_AMOUNT + " users ...");

        for (int i = 0 ; i < USER_AMOUNT ; i++) {
            var user = new RegistrationRequestDTO();

            String firstName = names.get(random.nextInt(names.size()));
            String lastName = names.get(random.nextInt(names.size()));
            String username = usernames.get(random.nextInt(usernames.size())) + usernames.get(random.nextInt(usernames.size()));
            String password = usernames.get(random.nextInt(usernames.size())) + random.nextInt(10) + symbols.get(random.nextInt(symbols.size()));

            LOGGER.info("Creating user " + (i + 1) + " with ...");
            LOGGER.info("First name: " + firstName);
            LOGGER.info("Last name: " + lastName);
            LOGGER.info("Username: " + username);
            LOGGER.info("Password: " + password);

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setPassword(password);

            service.registerUser(user);
        }
    }
}
