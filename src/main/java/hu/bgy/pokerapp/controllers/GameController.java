package hu.bgy.pokerapp.controllers;

import hu.bgy.pokerapp.dtos.TableSetupDTO;
import hu.bgy.pokerapp.dtos.TableDTO;
import hu.bgy.pokerapp.services.GameService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/v1/game")
@RestController
public class GameController {

    private final GameService gameService;

    @GetMapping("/info")
    public String info() {
        log.info("Info endpoint has been called");
        return "It is working!";
    }

    @PostMapping(value = "/create")
    public TableDTO creatPokerGame(@RequestBody @NonNull final TableSetupDTO tableSetup) {
        return gameService.createGame(tableSetup);
    }
}
