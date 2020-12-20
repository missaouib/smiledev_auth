package com.devcamp.project2.service;

import com.devcamp.project2.web.dto.MailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final RedisTemplate redisTemplate;
    private static final String fromMail = "wlsdn110@gmail.com";

    public String makeRandomString(){
        StringBuffer temp = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < 20; i++) {
            int rIndex = rnd.nextInt(3);
            switch (rIndex) {
                case 0:
                    // a-z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    // A-Z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    // 0-9
                    temp.append((rnd.nextInt(10)));
                    break;
            }
        }

        return temp.toString();
    }

    public void sendMail(MailDto mailDto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailDto.getSendTo());
        message.setFrom(fromMail);
        message.setSubject(mailDto.getTitle());
        message.setText(mailDto.getMessage());

        mailSender.send(message);
    }

    public void sendEmailVerifier(String sendTo,long id){
        String randomLink = makeRandomString();
        String verifiyLink = "localhost:8001/verify/"+randomLink;
        String message = "이 링크로 접속하시면 계정 인증이 됩니다. 24시간 안에 해주세요. \n" + verifiyLink;
        String title = "이메일 인증 안내";
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.opsForValue().set(randomLink,Long.toString(id));
        redisTemplate.expire(sendTo,1, TimeUnit.DAYS);
        sendMail(MailDto.builder().sendTo(sendTo).message(message).title(title).build());
    }//가입시 이메일 인증

    public void sendEmailFindPassword(String sendTo){
        int randomNum = (int) (Math.random() * 1000000);
        String message = "5분안에 인증번호를 입력해주세요. 인증번호는 다음과 같습니다. \n" + randomNum;
        String title = "비밀번호 인증번호";
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.opsForValue().set(sendTo, Integer.toString(randomNum));
        redisTemplate.expire(sendTo,5, TimeUnit.MINUTES);
        sendMail(MailDto.builder().sendTo(sendTo).message(message).title(title).build());
    }//가입시 이메일 인증
}
