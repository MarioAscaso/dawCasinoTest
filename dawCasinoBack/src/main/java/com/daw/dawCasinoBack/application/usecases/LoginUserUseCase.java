package com.daw.dawCasinoBack.application.usecases;

import com.daw.dawCasinoBack.domain.models.User;
import com.daw.dawCasinoBack.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User login(String username, String rawPassword) {
        // 1. Buscar usuario
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        User user = userOptional.get();

        // 2. Comprobar contraseña (¡La magia de BCrypt!)
        // matches(contraseña_plana, contraseña_encriptada_de_bd)
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // 3. Si todo va bien, devolvemos el usuario
        return user;
    }
}
