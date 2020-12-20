package com.devcamp.project2.web.dto.findPassword;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CertifyResponseDto {
    String certificationToken;
    String message;
    int code;

    CertifyResponseDto(String certificationToken,String message,int code){
        this.certificationToken=certificationToken;
        this.code=code;
        this.message=message;
    }
}
