package com.daw.dawCasinoBack.infrastructure.persistence.repositories;

import com.daw.dawCasinoBack.infrastructure.persistence.entities.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaGameRepository extends JpaRepository<GameEntity, String> {

    Optional<GameEntity> findByUserIdAndStatus(Long userId, String status);

    List<GameEntity> findByUserIdAndStatusNotOrderByCreatedAtDesc(Long userId, String status, Pageable pageable);
}