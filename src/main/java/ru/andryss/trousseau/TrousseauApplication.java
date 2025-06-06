package ru.andryss.trousseau;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
        exclude = {UserDetailsServiceAutoConfiguration.class}
)
public class TrousseauApplication {

    /**
     * Точка входа в приложение
     */
    public static void main(String[] args) {
        SpringApplication.run(TrousseauApplication.class, args);
    }

}
