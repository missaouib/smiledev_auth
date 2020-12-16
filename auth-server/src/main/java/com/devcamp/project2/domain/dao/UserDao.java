package com.devcamp.project2.domain.dao;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDao {
    long id;
    String uid;
    int verified;
    int status;
}
