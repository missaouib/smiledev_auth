package com.proeject2.apigateway.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController implements ErrorController {
    @Value("${error.path:/error}")
    private String errorPath;


    @Override
    public String getErrorPath() {
        return errorPath;
    }
}
