package com.umanbeing.umg.controllers.dto;

import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Setter
@Getter
public class CreateGameRequest {

    @NotBlank
    private Integer totalRounds;

    @NotBlank
    private Integer maxTimerSeconds;
    
    private Long userId;

}