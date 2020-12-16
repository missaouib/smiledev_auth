package com.devcamp.project2.web.dto;

import com.devcamp.project2.domain.login.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminDto {
    int code;
    String message;
    List<User> userList;

    @Builder
    AdminDto(int code,String message,List userList){
        this.code=code;
        this.message=message;
        this.userList=userList;
    }

    AdminDto(int code,String message){
        this.code=code;
        this.message=message;
    }
}
