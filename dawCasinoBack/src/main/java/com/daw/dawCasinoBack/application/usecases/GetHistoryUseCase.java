package com.daw.dawCasinoBack.application.usecases;

import com.daw.dawCasinoBack.infrastructure.controllers.dtos.GameHistoryResponse;
import com.daw.dawCasinoBack.infrastructure.persistence.repositories.JpaGameRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetHistoryUseCase {

    private final JpaGameRepository gameRepository;

    public GetHistoryUseCase(JpaGameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<GameHistoryResponse> getUserHistory(Long userId) {
        // Pedimos las 10 últimas que NO estén en curso ("PLAYING")
        return gameRepository.findByUserIdAndStatusNotOrderByCreatedAtDesc(
                        userId,
                        "PLAYING",
                        PageRequest.of(0, 10)
                ).stream()
                .map(game -> new GameHistoryResponse(
                        game.getId(),
                        game.getStatus(),
                        game.getBetAmount(),
                        game.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}