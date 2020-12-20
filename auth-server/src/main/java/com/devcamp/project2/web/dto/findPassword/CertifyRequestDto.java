package com.devcamp.project2.web.dto.findPassword;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CertifyRequestDto {
    String email;
    int certify;

    CertifyRequestDto(int certify, String email){
        this.certify=certify;
        this.email=email;
    }
}
