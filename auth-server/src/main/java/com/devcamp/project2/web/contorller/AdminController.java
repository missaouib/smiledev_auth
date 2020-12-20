package com.devcamp.project2.web.contorller;

import com.devcamp.project2.service.AdminService;
import com.devcamp.project2.web.dto.admin.AdminDeleteRequestDto;
import com.devcamp.project2.web.dto.admin.AdminResponseDto;
import com.devcamp.project2.web.dto.CommonResponseDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/getUsetList")
    AdminResponseDto manageUserController(@NonNull @RequestHeader(value = "token") String token){
        System.out.println();
        return adminService.getUserList(token);
    }

    @PostMapping("/deleteUser")
    CommonResponseDto deleteUserController(@NonNull @RequestHeader(value = "token") String token,
                                           @RequestBody AdminDeleteRequestDto adminDeleteRequestDto){
        return adminService.deleteUser(token,adminDeleteRequestDto);
    }
}
