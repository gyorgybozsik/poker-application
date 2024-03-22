package hu.bgy.pokerapp;

import hu.bgy.pokerapp.Utilities.InputReader;
import hu.bgy.pokerapp.models.Card;
import hu.bgy.pokerapp.models.Deck;
import hu.bgy.pokerapp.models.Table;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@SpringBootApplication
public class PokerAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(PokerAppApplication.class, args);
    }
}
