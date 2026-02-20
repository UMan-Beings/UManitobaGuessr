package com.umanbeing.umg.controllers.dto;

public class CreateGameRequest {
    private Integer totalRounds;
    private Integer maxTimerSeconds;
    private Long userId;

    // Getters and setters
    public Integer getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(Integer totalRounds) {
        this.totalRounds = totalRounds;
    }

    public Integer getMaxTimerSeconds() {
        return maxTimerSeconds;
    }

    public void setMaxTimerSeconds(Integer maxTimerSeconds) {
        this.maxTimerSeconds = maxTimerSeconds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}