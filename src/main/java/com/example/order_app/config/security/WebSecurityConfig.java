package com.example.order_app.config.security;

import com.example.order_app.config.oath2.CustomOAuth2UserService;
import com.example.order_app.security.handler.CustomLoginSuccessHandler;
import com.example.order_app.security.handler.CustomOAuth2LoginSuccessHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Getter
public class WebSecurityConfig {

    private final CustomLoginSuccessHandler loginSuccessHandler;
    private final CustomOAuth2UserService oAuth2UserService;
    private final CustomOAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
}
