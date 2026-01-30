package com.daw.dawCasinoBack.infrastructure.persistence;

import com.daw.dawCasinoBack.domain.models.User;
import com.daw.dawCasinoBack.domain.repositories.UserRepository;
import com.daw.dawCasinoBack.infrastructure.persistence.entities.UserEntity;
import com.daw.dawCasinoBack.infrastructure.persistence.repositories.JpaUserRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public UserRepositoryAdapter(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id).map(this::toDomain);
    }

    // 🔥🔥 AQUÍ ESTABA EL ERROR: FALTABA IMPLEMENTAR ESTOS MÉTODOS 🔥🔥
    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }
    // ---------------------------------------------------------------

    // --- MAPPERS ---

    private User toDomain(UserEntity entity) {
        LocalDateTime date = null;
        if (entity.getCreatedAt() != null) {
            date = LocalDateTime.parse(entity.getCreatedAt(), FORMATTER);
        }

        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getBalance(),
                entity.getRole(),
                entity.getAvatar(),
                entity.getAvatarType(),
                entity.getDailyLossLimit(),
                entity.getSessionTimeLimit(),
                date
        );
    }

    private UserEntity toEntity(User user) {
        String dateString = null;
        if (user.getCreatedAt() != null) {
            dateString = user.getCreatedAt().format(FORMATTER);
        }

        return new UserEntity(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getBalance(),
                user.getRole(),
                user.getAvatar(),
                user.getAvatarType(),
                user.getDailyLossLimit(),
                user.getSessionTimeLimit(),
                dateString
        );
    }
}