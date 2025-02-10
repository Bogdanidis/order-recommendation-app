package com.example.order_app.controller.rest;


import com.example.order_app.exception.AlreadyExistsException;
import com.example.order_app.model.User;
import com.example.order_app.request.AddUserRequest;
import com.example.order_app.request.LoginRequest;
import com.example.order_app.response.ApiResponse;
import com.example.order_app.response.JwtResponse;
import com.example.order_app.security.jwt.JwtUtils;
import com.example.order_app.security.user.ShopUserDetails;
import com.example.order_app.service.role.IRoleService;
import com.example.order_app.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthRestController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final IUserService userService;
    private final IRoleService roleService;

    /**
     * User login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            //SecurityContextHolder.getContext().setAuthentication(authentication);

            ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
            String jwt = jwtUtils.generateTokenForUser(authentication);

            JwtResponse response = new JwtResponse(userDetails.getId(), jwt);
            return ResponseEntity.ok(new ApiResponse<>("Login successful", response));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(UNAUTHORIZED)
                    .body(new ApiResponse<>("Invalid credentials", null));
        }
    }

    /**
     * User registration
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(
            @Valid @RequestBody AddUserRequest request) {
        try {
            // Set default user role
            request.setRoles(new HashSet<>(roleService.findByName("ROLE_USER")));

            User user = userService.addUser(request);
            return ResponseEntity.ok(new ApiResponse<>(
                    "Registration successful", userService.convertUserToDto(user)));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT)
                    .body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Registration failed: " + e.getMessage(), null));
        }
    }

    /**
     * Validate JWT token
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<?>> validateToken(
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                boolean isValid = jwtUtils.validateToken(jwt);
                return ResponseEntity.ok(new ApiResponse<>("Token is valid", isValid));
            }
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Invalid authorization header", null));
        } catch (Exception e) {
            return ResponseEntity.status(UNAUTHORIZED)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }
}
