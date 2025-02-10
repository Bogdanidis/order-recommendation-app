package com.example.order_app.controller.rest;

import com.example.order_app.dto.UserDto;
import com.example.order_app.exception.AlreadyExistsException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.exception.UnauthorizedOperationException;
import com.example.order_app.model.User;
import com.example.order_app.request.AddUserRequest;
import com.example.order_app.request.UpdateUserRequest;
import com.example.order_app.response.ApiResponse;
import com.example.order_app.response.SearchResponse;
import com.example.order_app.service.role.IRoleService;
import com.example.order_app.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
public class UserRestController {
    private final IUserService userService;
    private final IRoleService roleService;

    /**
     * Get all users with pagination and search (Admin only)
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SearchResponse<UserDto>> searchUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Page<UserDto> userPage = userService.findAllPaginated(
                PageRequest.of(page, size), search);

        return ResponseEntity.ok(new SearchResponse<>(
                "Users found", userPage, search));
    }

    /**
     * Get user details
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            User currentUser = userService.getAuthenticatedUser();

            boolean isOwnProfile = currentUser.getId().equals(userId);
            boolean isAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

            if (!isOwnProfile && !isAdmin) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new ApiResponse<>("You don't have permission to view this profile", null));
            }

            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new ApiResponse<>("User found", userDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }
    /**
     * Add new user (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> addUser(
            @Valid @RequestBody AddUserRequest request) {
        try {
            if (request.getRoles() == null || request.getRoles().isEmpty()) {
                request.setRoles(roleService.findByName("ROLE_USER"));
            }

            User user = userService.addUser(request);
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new ApiResponse<>("User created successfully", userDto));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Update user
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            User currentUser = userService.getAuthenticatedUser();
            boolean isOwnProfile = currentUser.getId().equals(userId);
            boolean isAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

            if (!isOwnProfile && !isAdmin) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new ApiResponse<>("You don't have permission to update this profile", null));
            }

            User user = userService.updateUser(request, userId);
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new ApiResponse<>("User updated successfully", userDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }
    /**
     * Delete user (Admin only)
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new ApiResponse<>("User deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(FORBIDDEN)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }
}