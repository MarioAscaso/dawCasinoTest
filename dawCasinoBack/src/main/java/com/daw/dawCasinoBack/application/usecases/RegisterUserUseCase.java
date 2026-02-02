package com.daw.dawCasinoBack.application.usecases;

import com.daw.dawCasinoBack.domain.models.User;
import com.daw.dawCasinoBack.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        user.setRole("USER");
        user.setBalance(1000.00);

        if (user.getAvatar() == null && user.getAvatarType() == null) {
            user.setAvatar("https://api.dicebear.com/9.x/avataaars/svg?seed=" + user.getUsername());
            user.setAvatarType("IMAGE");
        }

        return userRepository.save(user);
    }
}