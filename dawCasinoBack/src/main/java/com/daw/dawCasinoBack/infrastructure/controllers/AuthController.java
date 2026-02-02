package com.daw.dawCasinoBack.infrastructure.controllers;

import com.daw.dawCasinoBack.application.usecases.LoginUserUseCase;
import com.daw.dawCasinoBack.application.usecases.RegisterUserUseCase;
import com.daw.dawCasinoBack.domain.models.User;
import com.daw.dawCasinoBack.infrastructure.controllers.dtos.LoginRequest;
import com.daw.dawCasinoBack.infrastructure.controllers.dtos.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, LoginUserUseCase loginUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User userToRegister = new User(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    null,
                    null
            );

            userToRegister.setAvatar(request.getAvatar());
            userToRegister.setAvatarType(request.getAvatarType());

            User createdUser = registerUserUseCase.register(userToRegister);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = loginUserUseCase.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}