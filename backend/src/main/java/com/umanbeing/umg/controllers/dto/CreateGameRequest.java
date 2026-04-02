package com.umanbeing.umg.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object for creating a new game.
 * It is used for transferring game creation data between the controller and service layers.
 * <p>It must contain:<li>a valid number of rounds</li><li>a valid maximum timer duration in seconds</li><li>an optional user ID</li></p>
 */
@Setter
@Getter
public class CreateGameRequest {

    @NotBlank
    private Integer totalRounds;

    @NotBlank
    private Integer maxTimerSeconds;

    private Long userId;

}