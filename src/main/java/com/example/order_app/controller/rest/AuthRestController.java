package com.example.order_app.controller.rest;


import com.example.order_app.exception.AlreadyExistsException;
import com.example.order_app.model.User;
import com.example.order_app.request.AddUserRequest;
import com.example.order_app.request.LoginRequest;
import com.example.order_app.response.RestResponse;
import com.example.order_app.response.JwtResponse;
import com.example.order_app.security.jwt.JwtUtils;
import com.example.order_app.security.user.ShopUserDetails;
import com.example.order_app.service.role.IRoleService;
import com.example.order_app.service.user.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthRestController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final IUserService userService;
    private final IRoleService roleService;

    /**
     * User login
     */

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<RestResponse<?>> login(@Valid @RequestBody LoginRequest request) {
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
            return ResponseEntity.ok(new RestResponse<>("Login successful", response));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(UNAUTHORIZED)
                    .body(new RestResponse<>("Invalid credentials", null));
        }
    }

    /**
     * User registration
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<RestResponse<?>> register(
            @Valid @RequestBody AddUserRequest request) {
        try {
            // Set default user role
            request.setRoles(new HashSet<>(roleService.findByName("ROLE_USER")));

            User user = userService.addUser(request);
            return ResponseEntity.ok(new RestResponse<>(
                    "Registration successful", userService.convertUserToDto(user)));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT)
                    .body(new RestResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new RestResponse<>("Registration failed: " + e.getMessage(), null));
        }
    }

    /**
     * Validate JWT token
     */
    @GetMapping("/validate")
    @Operation(summary = "Validate JWT token", description = "Check if provided JWT token is valid")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "401", description = "Invalid token")
    })
    public ResponseEntity<RestResponse<?>> validateToken(
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                boolean isValid = jwtUtils.validateToken(jwt);
                return ResponseEntity.ok(new RestResponse<>("Token is valid", isValid));
            }
            return ResponseEntity.badRequest()
                    .body(new RestResponse<>("Invalid authorization header", null));
        } catch (Exception e) {
            return ResponseEntity.status(UNAUTHORIZED)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }
}
