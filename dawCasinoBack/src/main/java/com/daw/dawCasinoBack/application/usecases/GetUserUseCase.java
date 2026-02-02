package com.daw.dawCasinoBack.application.usecases;

import com.daw.dawCasinoBack.domain.models.User;
import com.daw.dawCasinoBack.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class GetUserUseCase {

    private final UserRepository userRepository;

    public GetUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}