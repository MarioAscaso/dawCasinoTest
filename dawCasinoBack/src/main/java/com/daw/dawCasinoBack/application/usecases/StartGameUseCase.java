package com.daw.dawCasinoBack.application.usecases;

import com.daw.dawCasinoBack.domain.models.User;
import com.daw.dawCasinoBack.domain.models.blackjack.BlackJackGame;
import com.daw.dawCasinoBack.domain.repositories.UserRepository;
import com.daw.dawCasinoBack.infrastructure.persistence.entities.GameEntity;
import com.daw.dawCasinoBack.infrastructure.persistence.repositories.JpaGameRepository;
import com.daw.dawCasinoBack.shared.utils.JsonUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StartGameUseCase {

    private final JpaGameRepository gameRepository;
    private final UserRepository userRepository;
    private final JsonUtils jsonUtils;

    public StartGameUseCase(JpaGameRepository gameRepository, UserRepository userRepository, JsonUtils jsonUtils) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.jsonUtils = jsonUtils;
    }

    public BlackJackGame startGame(Long userId, Double betAmount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (user.getBalance() < betAmount) {
            throw new RuntimeException("Saldo insuficiente");
        }

        Optional<GameEntity> existingGame = gameRepository.findByUserIdAndStatus(userId, "PLAYING");

        if (existingGame.isPresent()) {
            GameEntity oldGame = existingGame.get();
            oldGame.setStatus("ABANDONED");
            gameRepository.save(oldGame);
        }

        user.setBalance(user.getBalance() - betAmount);
        userRepository.save(user);

        BlackJackGame game = new BlackJackGame(userId, betAmount);

        String gameJson = jsonUtils.serialize(game);

        GameEntity entity = new GameEntity(
                game.getId(),
                userId,
                game.getStatus().toString(),
                betAmount,
                gameJson
        );

        gameRepository.save(entity);
        return game;
    }
}