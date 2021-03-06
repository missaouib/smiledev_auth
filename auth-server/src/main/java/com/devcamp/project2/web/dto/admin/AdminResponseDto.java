package com.devcamp.project2.web.dto.admin;

import com.devcamp.project2.domain.login.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminResponseDto {
    int code;
    String message;
    List<User> userList;

    @Builder
    AdminResponseDto(int code,String message,List userList){
        this.code=code;
        this.message=message;
        this.userList=userList;
    }

    AdminResponseDto(int code,String message){
        this.code=code;
        this.message=message;
    }
}
