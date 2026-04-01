package com.umanbeing.umg.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateGameRequest {

    @NotBlank
    private Integer totalRounds;

    @NotBlank
    private Integer maxTimerSeconds;

    private Long userId;

}