package com.umanbeing.umg.controllers.dto;

import com.umanbeing.umg.domain.Role;

public record LoginResponse(
  long id,
  String name,
  String email,
  Role role 
){
    
}
