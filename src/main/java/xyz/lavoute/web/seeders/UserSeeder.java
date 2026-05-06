package xyz.lavoute.web.seeders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import xyz.lavoute.web.dto.RegistrationRequestDTO;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.UserRepository;
import xyz.lavoute.web.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
            "SnackAttacker", "CaptainGeneral", "ObviousChoice", "WiFiWhisperer", "Whisperer", "NoodleNinja", "NinjaWarrior",
            "CouchCommander", "Commander", "BurritoBoss", "BossMaster", "PenguinPatrol", "PatrolUnit", "PixelArtist",
            "PirateKing", "TacoTornado", "TornadoWatch", "WaffleWarrior", "WarriorLegend", "LlamaDrama", "DramaQueen",
            "ToastMaster", "MasterMind", "CookieMonster", "MonsterHunter", "JuniorMember", "BananaBandana", "BandanaWearer",
            "MuffinManiac", "ManiacMode", "SleepySloth", "SlothExpert", "ThunderPants", "PantsParty", "PickleTickle",
            "TickleMonster", "GigglesMcGee", "McGeeSpecialist", "SnoreStation", "ChampionPlayer", "PancakePete", "PeteProfessional",
            "DonutDeputy", "DeputyMarshall", "PizzaProphet", "ProphetVision", "NachoNormal", "NormalCitizen", "YawnEnthusiast",
            "PatrolSquad", "BlanketBurglar", "BurglarAlarm", "SpudSpecialist", "StudService", "CheeseWhiz", "WhizWizard",
            "SnackSnatcher", "SnatcherPro", "WaddleWalker", "WalkerTexas", "BubbleTrouble", "TroubleMaker", "NapEnthusiast",
            "Enthusiast", "MemeLordHigh", "LordCommander", "LazyLegend", "LegendaryOne", "BurpBoss", "BossLevel"
    );

    private final List<String> symbols = List.of("!", "]", "+", ")", "*", "=", "(", "}", "{", "[", "&", "$");

    private final Random random = new Random();
    private UserService userService;
    private UserRepository userRepository;

    private List<User> users = new ArrayList<>();

    public UserSeeder(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    private String getUniqueName() {
        Random rng = new Random();
        int number = rng.nextInt(10000); // 0 to 9999
        String formatted = String.format("%04d", number);

        return names.get(random.nextInt(names.size())) + formatted;
    }

    private void createDefaultUsers() {
        var adminRegisterRequest = new RegistrationRequestDTO(
                "matanteAdmin",
                getUniqueName(),
                getUniqueName(),
                "Matante123!"
        );
        User adminUser = userService.registerUser(adminRegisterRequest);
        adminUser.setIsAdmin(true);
        User updatedAdmin = userRepository.save(adminUser);
        users.add(updatedAdmin);
        LOGGER.info("Admin created: " + updatedAdmin.toString());

        var firstUserRegisterRequest = new RegistrationRequestDTO(
                "usager1",
                getUniqueName(),
                getUniqueName(),
                "Lavoute1!"
        );
        User firstUser = userService.registerUser(firstUserRegisterRequest);
        users.add(firstUser);
        LOGGER.info("Default User 1 created: " + firstUser.toString());

        var secondUserRegisterRequest = new RegistrationRequestDTO(
                "usager2",
                getUniqueName(),
                getUniqueName(),
                "Lavoute2!"
        );
        User secondUser = userService.registerUser(secondUserRegisterRequest);
        users.add(secondUser);
        LOGGER.info("Default User 2 created: " + secondUser.toString());
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("User Seeder running ...");
        LOGGER.info("Seeder will create some default users ...");

        Optional<User> admin = userService.getUserByUsername("matanteAdmin");
        Optional<User> firstUser = userService.getUserByUsername("usager1");
        Optional<User> secondUser = userService.getUserByUsername("usager2");

        if (admin.isEmpty() && firstUser.isEmpty() && secondUser.isEmpty()) {
            createDefaultUsers();
        }

        LOGGER.info("Seeder will create " + USER_AMOUNT + " users");
        for (int i = 0 ; i < USER_AMOUNT ; i++) {
            String firstName = getUniqueName();
            String lastName = getUniqueName();
            String username = usernames.get(random.nextInt(usernames.size())) + usernames.get(random.nextInt(usernames.size()));
            String password = usernames.get(random.nextInt(usernames.size())) + random.nextInt(10) + symbols.get(random.nextInt(symbols.size()));

            var userRegitrationRequest = new RegistrationRequestDTO(firstName, lastName, username, password);

            LOGGER.info("User " + (i + 1) + " with ...");
            LOGGER.info(userRegitrationRequest.toString());
            LOGGER.info("was created");

            User savedUser = userService.registerUser(userRegitrationRequest);
            users.add(savedUser);
        }

        LOGGER.info("Here are all the users created by the seeder:");
        LOGGER.info(users.toString());
    }
}
