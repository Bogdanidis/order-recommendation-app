package com.example.order_app.config.oath2;

import com.example.order_app.repository.RoleRepository;
import com.example.order_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuth2UserServiceFactory {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService createOAuth2UserService() {
        return new CustomOAuth2UserService(
                userRepository,
                roleRepository,
                passwordEncoder
        );
    }
}