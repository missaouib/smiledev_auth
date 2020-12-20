package com.devcamp.project2.web.dto.login;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequestDto {
    private String uid;
    private String password;

    JoinRequestDto(String uid, String password) {
        this.uid = uid;
        this.password = password;
    }
}
