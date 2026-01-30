package com.daw.dawCasinoBack.application.usecases;

import com.daw.dawCasinoBack.domain.models.User;
import com.daw.dawCasinoBack.domain.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DepositMoneyUseCase {

    private final UserRepository userRepository;

    public DepositMoneyUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User deposit(Long userId, Double amount) {
        if (amount <= 0) {
            throw new RuntimeException("La cantidad debe ser positiva");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Sumamos el saldo
        user.setBalance(user.getBalance() + amount);

        return userRepository.save(user);
    }
}