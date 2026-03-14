package com.umanbeing.umg.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umanbeing.umg.controllers.dto.UserStatsResponse;
import com.umanbeing.umg.services.GameService;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private final GameService gameService;

    @GetMapping("/{userId}/stats")
    public ResponseEntity<UserStatsResponse> getUserStats(@PathVariable Long userId){
        Long totalScore = gameService.getUserTotalScore(userId);
        Long totalRounds = gameService.getUserTotalRounds(userId);
        Long totalGames = gameService.getUserTotalGames(userId);
        Long averageScore = gameService.getUserAverageScore(userId);
        UserStatsResponse response = new UserStatsResponse(totalScore, totalRounds, totalGames, averageScore);
        return ResponseEntity.ok(response);
    }
}