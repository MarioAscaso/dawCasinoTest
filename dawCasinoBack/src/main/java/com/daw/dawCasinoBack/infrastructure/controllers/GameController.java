package com.daw.dawCasinoBack.infrastructure.controllers;

import com.daw.dawCasinoBack.application.usecases.GetHistoryUseCase;
import com.daw.dawCasinoBack.application.usecases.PlayTurnUseCase;
import com.daw.dawCasinoBack.application.usecases.StartGameUseCase;
import com.daw.dawCasinoBack.domain.models.blackjack.BlackJackGame;
import com.daw.dawCasinoBack.infrastructure.controllers.dtos.GameActionRequest;
import com.daw.dawCasinoBack.infrastructure.controllers.dtos.GameResponse;
import com.daw.dawCasinoBack.infrastructure.controllers.dtos.StartGameRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GameController {

    private final StartGameUseCase startGameUseCase;
    private final PlayTurnUseCase playTurnUseCase;
    private final GetHistoryUseCase getHistoryUseCase;

    public GameController(StartGameUseCase startGameUseCase, PlayTurnUseCase playTurnUseCase, GetHistoryUseCase getHistoryUseCase) {
        this.startGameUseCase = startGameUseCase;
        this.playTurnUseCase = playTurnUseCase;
        this.getHistoryUseCase = getHistoryUseCase;
    }

    @PostMapping("/blackjack/start")
    public ResponseEntity<?> startGame(@RequestBody StartGameRequest request) {
        try {
            BlackJackGame game = startGameUseCase.startGame(request.getUserId(), request.getBetAmount());
            return ResponseEntity.ok(toDto(game));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/blackjack/play")
    public ResponseEntity<?> playTurn(@RequestBody GameActionRequest request) {
        try {
            BlackJackGame game = playTurnUseCase.playTurn(request.getGameId(), request.getAction());
            return ResponseEntity.ok(toDto(game));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(getHistoryUseCase.getUserHistory(userId));
    }

    private GameResponse toDto(BlackJackGame game) {
        return new GameResponse(
                game.getId(),
                game.getStatus(),
                game.getPlayerHand(),
                game.getDealerHand(),
                game.getBetAmount()
        );
    }
}