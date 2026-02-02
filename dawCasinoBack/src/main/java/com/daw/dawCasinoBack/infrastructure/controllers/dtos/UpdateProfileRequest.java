package com.daw.dawCasinoBack.infrastructure.controllers.dtos;

public class UpdateProfileRequest {
    private Long userId;
    private String avatar;
    private String avatarType;
    private Double dailyLossLimit;
    private Integer sessionTimeLimit;

    public UpdateProfileRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getAvatarType() { return avatarType; }
    public void setAvatarType(String avatarType) { this.avatarType = avatarType; }

    public Double getDailyLossLimit() { return dailyLossLimit; }
    public void setDailyLossLimit(Double dailyLossLimit) { this.dailyLossLimit = dailyLossLimit; }

    public Integer getSessionTimeLimit() { return sessionTimeLimit; }
    public void setSessionTimeLimit(Integer sessionTimeLimit) { this.sessionTimeLimit = sessionTimeLimit; }
}