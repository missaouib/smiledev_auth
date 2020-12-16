package com.devcamp.project2.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.devcamp.project2.domain.login.User;
import com.devcamp.project2.domain.login.UserRepository;
import com.devcamp.project2.web.dto.JoinRequestDto;
import com.devcamp.project2.web.dto.JoinResponseDto;
import com.devcamp.project2.web.dto.LoginRequestDto;
import com.devcamp.project2.web.dto.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    Logger loggerFactory = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisTemplate redisTemplate;

    @Value("${jwtsecret}")
    private String secret;

    @Transactional
    public String emailVerify(String link){
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        if(redisTemplate.opsForValue().get(link)!=null){
            User user = userRepository.findFirstById(Long.parseLong(redisTemplate.opsForValue().get(link).toString()));
            user.update(1);
            return "verify finish";
        }else{
            return "링크가 없거나 만료되었습니다. 만료되었을 로그인 하시면 이메일을 재발송 해드립니다.";
        }
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            User user = userRepository.findFirstByUid(loginRequestDto.getUid());//아이디 존재 여부 확
            if (user == null)
                return LoginResponseDto.builder().code(201).message("Id 또는 비밀번호를 확인해주세요.").build();
            else if (!BCrypt.checkpw(loginRequestDto.getPassword() + user.getSalt(), user.getPassword())) {
                return LoginResponseDto.builder().code(201).message("Id 또는 비밀번호를 확인해주세요.").build();
            }else if(user.getVerified()==0){
                emailService.sendEmailVerifier(user.getUid(),user.getId());
                return LoginResponseDto.builder().code(202).message("이메일 인증을 먼저 해주세요").build();
            }
            else {
                String token = JWT.create().withClaim("id", user.getId())
                        .withClaim("status", user.getStatus())
                        .withClaim("uid", user.getUid())
                        .withIssuer("kwon")
                        .withExpiresAt(makeTime(0))
                        .sign(algorithm);

                String refreshToken = JWT.create()
                        .withIssuer("kwon")
                        .withExpiresAt(makeTime(1))
                        .sign(algorithm);
                redisTemplate.setKeySerializer(new StringRedisSerializer());
                redisTemplate.setValueSerializer(new StringRedisSerializer());
                redisTemplate.opsForValue().set(Long.toString(user.getId()), refreshToken);
                redisTemplate.expire(Long.toString(user.getId()),1, TimeUnit.DAYS);

                return LoginResponseDto.builder().code(200).message("success").token(token).refreshToken(refreshToken).build();
            }
        } catch (JWTCreationException e) {
            loggerFactory.error("Class {}", this.getClass(), e);
            return LoginResponseDto.builder().code(203).message("Create Error").build();
        }
    }

    public JoinResponseDto join(JoinRequestDto joinRequestDto) {
        try {
            String salt = BCrypt.gensalt(8);
            String hashedPassword = BCrypt.hashpw(joinRequestDto.getPassword() + salt, BCrypt.gensalt());
            User user = userRepository.save(User.builder().uid(joinRequestDto.getUid()).password(hashedPassword).salt(salt).build());
            emailService.sendEmailVerifier(user.getUid(),user.getId());
            return JoinResponseDto.builder().code(200).message("success").build();
        } catch (Exception e) {
            loggerFactory.error("Class {}", getClass(), e);
            return JoinResponseDto.builder().code(201).message("join error").build();
        }
    }

    private Date makeTime(int timezone) {
        Calendar cal = Calendar.getInstance();
        Date expire_date = new Date();
        cal.setTime(expire_date);
        if (timezone == 0)
            cal.add(Calendar.MINUTE, 40);
        else
            cal.add(Calendar.DATE, 1);
        expire_date = cal.getTime();
        return expire_date;
    }//토큰 유효기간
}
