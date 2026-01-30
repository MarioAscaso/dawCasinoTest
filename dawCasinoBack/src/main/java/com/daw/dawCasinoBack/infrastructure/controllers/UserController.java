package com.daw.dawCasinoBack.infrastructure.controllers;

import com.daw.dawCasinoBack.application.usecases.UpdateProfileUseCase;
import com.daw.dawCasinoBack.domain.models.User;
import com.daw.dawCasinoBack.infrastructure.controllers.dtos.UpdateProfileRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Para evitar problemas con el Frontend
public class UserController {

    private final UpdateProfileUseCase updateProfileUseCase;

    public UserController(UpdateProfileUseCase updateProfileUseCase) {
        this.updateProfileUseCase = updateProfileUseCase;
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request) {
        try {
            User updatedUser = updateProfileUseCase.updateProfile(
                    request.getUserId(),
                    request.getAvatar(),
                    request.getAvatarType(),
                    request.getDailyLossLimit(),
                    request.getSessionTimeLimit()
            );
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}