package com.devcamp.project2.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Builder
public class MailDto {
    private String sendTo;
    private String title;
    private String message;
}
