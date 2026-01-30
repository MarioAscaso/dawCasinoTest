package com.daw.dawCasinoBack.application.usecases;

import com.daw.dawCasinoBack.domain.models.User;
import com.daw.dawCasinoBack.domain.models.blackjack.BlackJackGame;
import com.daw.dawCasinoBack.domain.models.blackjack.GameStatus;
import com.daw.dawCasinoBack.domain.repositories.UserRepository;
import com.daw.dawCasinoBack.infrastructure.persistence.entities.GameEntity;
import com.daw.dawCasinoBack.infrastructure.persistence.repositories.JpaGameRepository;
import com.daw.dawCasinoBack.shared.utils.JsonUtils;
import org.springframework.stereotype.Service;

@Service
public class PlayTurnUseCase {

    private final JpaGameRepository gameRepository;
    private final UserRepository userRepository;
    private final JsonUtils jsonUtils;

    public PlayTurnUseCase(JpaGameRepository gameRepository, UserRepository userRepository, JsonUtils jsonUtils) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.jsonUtils = jsonUtils;
    }

    public BlackJackGame playTurn(String gameId, String action) {
        // 1. Recuperar la partida de la BBDD
        GameEntity entity = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        // 2. Descongelar: JSON String -> Objeto Java
        BlackJackGame game = jsonUtils.deserialize(entity.getGameState(), BlackJackGame.class);

        // 3. Ejecutar la acción
        if ("HIT".equalsIgnoreCase(action)) {
            game.playerHit();
        } else if ("STAND".equalsIgnoreCase(action)) {
            game.playerStand();
        } else {
            throw new RuntimeException("Acción no válida. Usa HIT o STAND");
        }

        // 4. Si la partida ha terminado, gestionar PAGOS 💸
        if (game.getStatus() != GameStatus.PLAYING) {
            handleGameEnd(game);
        }

        // 5. Actualizar entidad y volver a congelar
        entity.setGameState(jsonUtils.serialize(game));
        entity.setStatus(game.getStatus().toString());
        gameRepository.save(entity);

        return game;
    }

    private void handleGameEnd(BlackJackGame game) {
        // Buscamos al usuario para pagarle (o no)
        User user = userRepository.findById(game.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (game.getStatus() == GameStatus.PLAYER_WINS) {
            // Ganaste: Recuperas lo apostado + beneficio (x2)
            // Ejemplo: Apostaste 50, te doy 100 (ganancia neta 50)
            double winnings = game.getBetAmount() * 2;

            // Si es BlackJack natural (21 con 2 cartas), se suele pagar 3 a 2 (x2.5)
            // Pero para simplificar el TFG, lo dejamos en x2 por ahora.

            user.setBalance(user.getBalance() + winnings);

        } else if (game.getStatus() == GameStatus.DRAW) {
            // Empate: Recuperas lo apostado
            user.setBalance(user.getBalance() + game.getBetAmount());
        }
        // Si pierdes (DEALER_WINS), no hacemos nada (el dinero ya se restó al empezar)

        userRepository.save(user);
    }
}