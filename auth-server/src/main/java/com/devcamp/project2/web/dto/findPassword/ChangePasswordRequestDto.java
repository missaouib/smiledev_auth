package com.devcamp.project2.web.dto.findPassword;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequestDto {
    String certifyToken;
    String password;

    ChangePasswordRequestDto(String certifyToken,String password){
        this.certifyToken=certifyToken;
        this.password=password;
    }
}
