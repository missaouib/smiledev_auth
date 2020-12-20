package com.devcamp.project2.web.dto.login;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MakeTokenRequestDto {
    String token;
    String refreshToken;

    @Builder
    MakeTokenRequestDto(String token,String refreshToken){
        this.token=token;
        this.refreshToken=refreshToken;
    }
}
