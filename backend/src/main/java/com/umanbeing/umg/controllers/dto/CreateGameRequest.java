package com.umanbeing.umg.controllers.dto;

import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class CreateGameRequest {

    private Integer totalRounds;

    private Integer maxTimerSeconds;
    
    private Long userId;

}