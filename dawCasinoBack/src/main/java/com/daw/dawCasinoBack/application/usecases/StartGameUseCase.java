package com.daw.dawCasinoBack.application.usecases;

import com.daw.dawCasinoBack.domain.models.User;
import com.daw.dawCasinoBack.domain.models.blackjack.BlackJackGame;
import com.daw.dawCasinoBack.domain.repositories.UserRepository;
import com.daw.dawCasinoBack.infrastructure.persistence.entities.GameEntity;
import com.daw.dawCasinoBack.infrastructure.persistence.repositories.JpaGameRepository;
import com.daw.dawCasinoBack.shared.utils.JsonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Opcional pero recomendado para consistencia

import java.util.Optional; // Importante para el Optional

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
        // 1. Validar usuario y saldo
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getBalance() < betAmount) {
            throw new RuntimeException("Saldo insuficiente");
        }

        // 2. GESTIÓN DE PARTIDAS "ZOMBIES" (La corrección clave)
        // Verificamos si ya existe una partida en curso ("PLAYING")
        Optional<GameEntity> existingGame = gameRepository.findByUserIdAndStatus(userId, "PLAYING");

        if (existingGame.isPresent()) {
            // Si existe, la cancelamos/abandonamos para desbloquear al usuario
            GameEntity oldGame = existingGame.get();
            oldGame.setStatus("ABANDONED"); // Estado 'ABANDONED' para historial
            gameRepository.save(oldGame);

            // Nota: Aquí no devolvemos el dinero de la anterior, se considera perdida por abandono.
        }

        // 3. Descontar saldo (Apuesta)
        user.setBalance(user.getBalance() - betAmount);
        userRepository.save(user);

        // 4. Crear la lógica del juego (Barajar, repartir...)
        BlackJackGame game = new BlackJackGame(userId, betAmount);

        // 5. CONVERTIR A ENTIDAD DE BBDD (Serializar estado)
        String gameJson = jsonUtils.serialize(game);

        GameEntity entity = new GameEntity(
                game.getId(),
                userId,
                game.getStatus().toString(),
                betAmount,
                gameJson
        );

        gameRepository.save(entity);

        return game; // Devolvemos el objeto de dominio limpio
    }
}