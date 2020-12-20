package com.devcamp.project2.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CheckEmailRequestDto {
    String email;

    CheckEmailRequestDto(String email){
        this.email=email;
    }
}
