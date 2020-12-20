package com.devcamp.project2.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.devcamp.project2.domain.login.User;
import com.devcamp.project2.domain.login.UserRepository;
import com.devcamp.project2.web.dto.admin.AdminDeleteRequestDto;
import com.devcamp.project2.web.dto.admin.AdminResponseDto;
import com.devcamp.project2.web.dto.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;

    public long getIdFromToken(String token){
        if(token.compareTo("null")==0)
            return -1;
        DecodedJWT decodedJWT = JWT.decode(token);
        long id = decodedJWT.getClaim("id").asLong();
        return id;
    }

    public AdminResponseDto getUserList(String token){
        long id = getIdFromToken(token);
        if(id==-1){
            return AdminResponseDto.builder().code(201).message("권한이 없습니다.").build();
        }
        User user=userRepository.findFirstById(id);
        if(user.getStatus()==0){
            return AdminResponseDto.builder().code(201).message("권한이 없습니다.").build();
        }

        List<User> userList=userRepository.findAll();
        return AdminResponseDto.builder().code(200).message("success").userList(userList).build();
    }

    @Transactional
    public CommonResponseDto deleteUser(String token, AdminDeleteRequestDto adminDeleteRequestDto){
        try {
            long targetId = adminDeleteRequestDto.getUserId();
            long id = getIdFromToken(token);
            User user=userRepository.findFirstById(id);
            if(user==null){
                throw new Exception();
            }
            if(user.getStatus()==0){
                return CommonResponseDto.builder().code(201).message("권한이 없습니다.").build();
            }//권한이 없는 경

            if(userRepository.findFirstById(targetId)!=null) {
                userRepository.deleteById(targetId);
                return CommonResponseDto.builder().code(200).message("Success").build();
            }else {
                return CommonResponseDto.builder().code(202).message("no user").build();
            }
        }catch (Exception e){
            logger.error("{}",e);
            return CommonResponseDto.builder().code(500).message("error").build();
        }
    }
}
