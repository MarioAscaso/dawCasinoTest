package com.daw.dawCasinoBack.infrastructure.controllers.dtos;

public class RegisterRequest {

    private String username;
    private String email;
    private String password;

    // 🔥 NUEVOS CAMPOS
    private String avatar;      // URL imagen
    private String avatarType;  // Código bandera (ej: "🇪🇸") o "IMAGE"

    public RegisterRequest() {}

    public RegisterRequest(String username, String email, String password, String avatar, String avatarType) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.avatarType = avatarType;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getAvatarType() { return avatarType; }
    public void setAvatarType(String avatarType) { this.avatarType = avatarType; }
}