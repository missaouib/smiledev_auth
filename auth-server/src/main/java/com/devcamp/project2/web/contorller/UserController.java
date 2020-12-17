package com.devcamp.project2.web.contorller;

import com.devcamp.project2.service.AdminService;
import com.devcamp.project2.service.LoginService;
import com.devcamp.project2.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class UserController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LoginService loginService;

    @PostMapping("/login")
    LoginResponseDto loginController(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto returnValue = loginService.login(loginRequestDto);
        return returnValue;
    }

    @PostMapping("/join")
    JoinResponseDto joinController(@RequestBody JoinRequestDto joinRequestDto) {
        return loginService.join(joinRequestDto);
    }

    @GetMapping("/verify/{randomString}")
    String verifyEmailController(@PathVariable("randomString") String randomString){
        return loginService.emailVerify(randomString);
    }

    @PostMapping("/findPassword")
    String findPasswordController(@PathVariable("randomString") String randomString){
        return loginService.emailVerify(randomString);
    }



}
