package com.devcamp.project2.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponseDto {
    int code;
    String message;

    @Builder
    CommonResponseDto(int code,String message){
        this.code=code;
        this.message=message;
    }
}
