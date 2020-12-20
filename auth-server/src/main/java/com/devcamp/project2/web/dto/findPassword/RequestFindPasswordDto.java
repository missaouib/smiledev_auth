package com.devcamp.project2.web.dto.findPassword;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestFindPasswordDto {
    String uid;

    RequestFindPasswordDto(String uid){
        this.uid=uid;
    }
}
