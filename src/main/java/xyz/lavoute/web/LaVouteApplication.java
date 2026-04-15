package xyz.lavoute.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xyz.lavoute.web.repositories.UserRepository;

@SpringBootApplication
public class LaVouteApplication {

    private static final Logger logger = LoggerFactory.getLogger(LaVouteApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LaVouteApplication.class, args);
    }

}
