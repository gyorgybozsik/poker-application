package hu.bgy.pokerapp.controllers;
import hu.bgy.pokerapp.dtos.SpeakerActionDTO;
import hu.bgy.pokerapp.dtos.TableSetupDTO;
import hu.bgy.pokerapp.dtos.TableDTO;
import hu.bgy.pokerapp.services.GameService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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

    @GetMapping("/all/poker-game")
    public Set<Long> getAllPokerGame() {
        return gameService.getAllPokerTable();
    }

    @GetMapping("/load-table/{tableId}")
    public TableDTO loadTable(@PathVariable final long tableId){
        return gameService.loadGame(tableId);
    }

    @PutMapping("/table/{tableId}/speaker")
    public TableDTO performTableSpeaker(@RequestBody SpeakerActionDTO speakerActionDTO,
                                        @PathVariable long tableId) {
        return gameService.performTableSpeaker(speakerActionDTO, tableId);
    }
}
