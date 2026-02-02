package com.daw.dawCasinoBack.application.usecases;

import com.daw.dawCasinoBack.domain.models.User;
import com.daw.dawCasinoBack.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateProfileUseCase {

    private final UserRepository userRepository;

    public UpdateProfileUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User updateProfile(Long userId, String avatar, String avatarType, Double lossLimit, Integer timeLimit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (avatar != null) user.setAvatar(avatar);
        if (avatarType != null) user.setAvatarType(avatarType);

        if (lossLimit != null && lossLimit < 0) throw new RuntimeException("El límite de pérdidas no puede ser negativo");
        if (timeLimit != null && timeLimit < 0) throw new RuntimeException("El límite de tiempo no puede ser negativo");

        user.setDailyLossLimit(lossLimit);
        user.setSessionTimeLimit(timeLimit);

        return userRepository.save(user);
    }
}
