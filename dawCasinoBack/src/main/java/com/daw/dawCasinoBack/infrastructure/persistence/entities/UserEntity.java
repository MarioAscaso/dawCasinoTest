package com.daw.dawCasinoBack.infrastructure.persistence.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private Double balance;

    private String role;

    // --- NUEVOS CAMPOS DE PERSONALIZACIÓN Y JUEGO RESPONSABLE ---
    private String avatar; // URL de la imagen o Código de país

    @Column(name = "avatar_type")
    private String avatarType; // "IMAGE" o "FLAG"

    @Column(name = "daily_loss_limit")
    private Double dailyLossLimit; // Límite de dinero

    @Column(name = "session_time_limit")
    private Integer sessionTimeLimit; // Límite de minutos

    private String createdAt; // Se guarda como String para simplificar persistencia

    // Constructor vacío (Obligatorio para JPA)
    public UserEntity() {
    }

    // Constructor completo actualizado
    public UserEntity(Long id, String username, String email, String password, Double balance, String role,
                      String avatar, String avatarType, Double dailyLossLimit, Integer sessionTimeLimit, String createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.role = role;
        this.avatar = avatar;
        this.avatarType = avatarType;
        this.dailyLossLimit = dailyLossLimit;
        this.sessionTimeLimit = sessionTimeLimit;
        this.createdAt = createdAt;
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getAvatarType() { return avatarType; }
    public void setAvatarType(String avatarType) { this.avatarType = avatarType; }

    public Double getDailyLossLimit() { return dailyLossLimit; }
    public void setDailyLossLimit(Double dailyLossLimit) { this.dailyLossLimit = dailyLossLimit; }

    public Integer getSessionTimeLimit() { return sessionTimeLimit; }
    public void setSessionTimeLimit(Integer sessionTimeLimit) { this.sessionTimeLimit = sessionTimeLimit; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}