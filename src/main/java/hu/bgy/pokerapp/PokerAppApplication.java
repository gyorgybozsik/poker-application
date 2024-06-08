package hu.bgy.pokerapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class PokerAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(PokerAppApplication.class, args);
    }
}
