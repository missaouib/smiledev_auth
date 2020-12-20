package com.devcamp.project2.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.devcamp.project2.domain.login.User;
import com.devcamp.project2.domain.login.UserRepository;
import com.devcamp.project2.web.dto.*;
import com.devcamp.project2.web.dto.findPassword.CertifyRequestDto;
import com.devcamp.project2.web.dto.findPassword.CertifyResponseDto;
import com.devcamp.project2.web.dto.findPassword.ChangePasswordRequestDto;
import com.devcamp.project2.web.dto.findPassword.RequestFindPasswordDto;
import com.devcamp.project2.web.dto.login.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/*
Timezone 0 : AccessToken time
Timezone 1 : RefreshToken time
Timezone 2 : Password Change Token time
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService implements InitializingBean {

    @Value("${jwtsecret}")
    private String secret;
    Logger loggerFactory = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisTemplate redisTemplate;
    private Algorithm algorithm;

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

    public String makeAccessToken(User user){
        return JWT.create()
                .withClaim("id", user.getId())
                .withClaim("status", user.getStatus())
                .withClaim("uid", user.getUid())
                .withIssuer("kwon")
                .withExpiresAt(makeTime(0))
                .sign(algorithm);
    }

    public String makeAccessToken(String token){
        DecodedJWT decodedJWT = JWT.decode(token);

        return JWT.create()
                .withClaim("id", decodedJWT.getClaim("id").asLong())
                .withClaim("status", decodedJWT.getClaim("status").asInt())
                .withClaim("uid", decodedJWT.getClaim("uid").asString())
                .withIssuer("kwon")
                .withExpiresAt(makeTime(0))
                .sign(algorithm);
    }

    public boolean checkDuplicate(CheckEmailRequestDto checkEmailRequestDto){
        User user = userRepository.findFirstByUid(checkEmailRequestDto.getEmail());
        if(user==null){
            return true;
        }else{
            return false;
        }
    }//Same Email check

    public CommonResponseDto requestFindPassword(RequestFindPasswordDto requestFindPasswordDto){
        User user = userRepository.findFirstByUid(requestFindPasswordDto.getUid());
        if(user==null){
            return CommonResponseDto.builder().code(200).message("Email로 인증번호 전송을 완료하였습니다. \n 인증번호를 5분 안에 제출해 주세요.").build();
        }else{
            emailService.sendEmailFindPassword(requestFindPasswordDto.getUid());
            return CommonResponseDto.builder().code(200).message("Email로 인증번호 전송을 완료하였습니다. \n 인증번호를 5분 안에 제출해 주세요. ").build();
        }
    }

    public CertifyResponseDto checkCertification(CertifyRequestDto certifyRequestDto){
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Object object=redisTemplate.opsForValue().get(certifyRequestDto.getEmail());
        if(object==null){
            return CertifyResponseDto.builder().code(301).message("인증번호가 만료되었습니다. 처음부터 다시 시도해주세요.").build();
        }else if(certifyRequestDto.getEmail()==object.toString()){
            return CertifyResponseDto.builder().code(300).message("not correct").build();
        }else{
            String certificationToken=JWT.create().withExpiresAt(makeTime(2))
                    .withClaim("uid",certifyRequestDto.getEmail()).sign(algorithm);
            return CertifyResponseDto.builder()
                    .code(200)
                    .message("success")
                    .certificationToken(certificationToken).build();
        }
    }//인증번호 일치 검사

    @Transactional
    public CommonResponseDto changePassword(ChangePasswordRequestDto changePasswordRequestDto){
        String token = changePasswordRequestDto.getCertifyToken();
        DecodedJWT decodedJWT = JWT.decode(token);
        String email = decodedJWT.getClaim("uid").asString();
        User user = userRepository.findFirstByUid(email);
        user.changePassword(changePasswordRequestDto.getPassword());
        return CommonResponseDto.builder().message("change success").code(200).build();
    }//비밀번호 교체 함수

    public MakeTokenResponseDto getNewToken(MakeTokenRequestDto makeTokenRequestDto){
        String token = makeTokenRequestDto.getToken();
        String refreshToken = makeTokenRequestDto.getRefreshToken();
        DecodedJWT decodedJWT = JWT.decode(token);
        long id = decodedJWT.getClaim("id").asLong();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        Object object = redisTemplate.opsForValue().get(Long.toString(26));
        if(object==null){
            return MakeTokenResponseDto.builder().token(null).code(215).message("재로그인 해주세요.").build();
        }else if(!refreshToken.equals(object.toString())){
            return MakeTokenResponseDto.builder().token(null).code(220).message("토큰이 변조되었습니다. 재로그인해주세요").build();
        }else{
            String newToken = makeAccessToken(token);
            return MakeTokenResponseDto.builder().token(newToken).code(200).message("new token finish").build();
        }
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        try {
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
                String token = makeAccessToken(user);

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
            User user = userRepository.findFirstByUid(joinRequestDto.getUid());
            if(user!=null){
                return JoinResponseDto.builder().code(201).message("이미 존재하는 이메일입니다.").build();
            }
            String salt = BCrypt.gensalt(8);
            String hashedPassword = BCrypt.hashpw(joinRequestDto.getPassword() + salt, BCrypt.gensalt());
            user = userRepository.save(User.builder().uid(joinRequestDto.getUid()).password(hashedPassword).salt(salt).build());
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
        if (timezone == 0)//access token time
            cal.add(Calendar.MINUTE, 40);
        else if(timezone == 1)
            cal.add(Calendar.DATE, 1);
        else
            cal.add(Calendar.MINUTE,5);//비밀번호 변경 유효기간
        expire_date = cal.getTime();
        return expire_date;
    }//토큰 유효기간

    @Override
    public void afterPropertiesSet() throws Exception {
        this.algorithm=Algorithm.HMAC256(secret);
    }
}
