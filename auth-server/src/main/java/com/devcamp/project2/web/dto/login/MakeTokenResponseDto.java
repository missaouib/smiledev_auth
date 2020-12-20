package com.devcamp.project2.web.dto.login;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MakeTokenResponseDto {
    String token;
    int code;
    String message;

    @Builder
    MakeTokenResponseDto(int code, String message, String token){
        this.token=token;
        this.code=code;
        this.message=message;
    }
}
