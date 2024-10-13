package com.example.order_app.security.config;


import com.example.order_app.security.jwt.AuthTokenFilter;
import com.example.order_app.security.jwt.JwtAuthEntryPoint;
import com.example.order_app.security.user.ShopUserDetailsService;
import com.example.order_app.service.cart.ICartService;
import com.example.order_app.service.user.IUserService;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class ShopConfig {
    private final ShopUserDetailsService userDetailsService;
    private final JwtAuthEntryPoint authEntryPoint;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;


    private static final List<String> SECURED_URLS =
            List.of("/order-api/v1/carts/**", "/order-api/v1/cartItems/**");

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return  authConfig.getAuthenticationManager();

    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // JWT-based security for /api/** endpoints
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {

        http.securityMatcher("/order-api/v1/**");
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->auth
                        .requestMatchers(SECURED_URLS.toArray(String[]::new)).authenticated()
                        //.requestMatchers("/order-api/v1/**").authenticated()
                        .anyRequest().permitAll());
        http.authenticationProvider(daoAuthenticationProvider());
        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

    // Session-based security for non-API routes (Thymeleaf pages, etc.)
    @Bean
    @Order(2)
    public SecurityFilterChain sessionSecurityFilterChain(HttpSecurity http) throws Exception {

        // Session-based security for non-API routes (Thymeleaf pages, etc.)
        http
                .authenticationProvider(daoAuthenticationProvider())
                //.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/","/home", "/auth/**","/error").permitAll()  // Public access
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()  // Allow static resources
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // Admin only
                        .anyRequest().permitAll()
                        //.anyRequest().authenticated()  // Any other request requires authentication
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .successHandler(customLoginSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/home?logout=true")
                        .invalidateHttpSession(true)  // This ensures the session is invalidated
                        .deleteCookies("JSESSIONID")  // This deletes the session cookie
                        .permitAll()
                );

        return http.build();
    }
}