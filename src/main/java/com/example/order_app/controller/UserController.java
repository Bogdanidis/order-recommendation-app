package com.example.order_app.controller;

import com.example.order_app.dto.UserDto;
import com.example.order_app.exception.AlreadyExistsException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Role;
import com.example.order_app.model.User;
import com.example.order_app.request.CreateUserRequest;
import com.example.order_app.request.UpdateUserRequest;
import com.example.order_app.service.role.IRoleService;
import com.example.order_app.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final IUserService userService;
    private final IRoleService roleService;


    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {
        Page<UserDto> userPage = userService.findAllPaginated(PageRequest.of(page, size), search);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("search", search);
        return "user/list";
    }

    @GetMapping("/{userId}")
    public String getUserDetails(@PathVariable Long userId, Model model, Authentication authentication) {
        User user = userService.getUserById(userId);

        // Check if the logged-in user is viewing their own profile or if they're an admin
        boolean isOwnProfile = authentication != null &&
                authentication.getName().equals(user.getEmail());
        boolean isAdmin = authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwnProfile && !isAdmin) {
            return "redirect:/error";
        }

        model.addAttribute("user", user);
        model.addAttribute("isOwnProfile", isOwnProfile);
        return "user/details";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showCreateUserForm(Model model) {
        model.addAttribute("createUserRequest", new CreateUserRequest());
        List<Role> availableRoles = roleService.getAllRoles();
        model.addAttribute("availableRoles", availableRoles);
        return "user/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String createUser(@Valid @ModelAttribute CreateUserRequest createUserRequest,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<Role> availableRoles = roleService.getAllRoles();
            model.addAttribute("availableRoles", availableRoles);
            return "user/add";
        }

        try {
            User createdUser = userService.createUser(createUserRequest);
            redirectAttributes.addFlashAttribute("message", "User created successfully");
            return "redirect:/users/" + createdUser.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/add";
        }
    }

    @GetMapping("/{userId}/update")
    public String showUpdateUserForm(@PathVariable Long userId, Model model, Authentication authentication) {
        User user = userService.getUserById(userId);
        boolean isOwnProfile = authentication.getName().equals(user.getEmail());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwnProfile && !isAdmin) {
            return "redirect:/error";
        }

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName(user.getFirstName());
        updateRequest.setLastName(user.getLastName());
        updateRequest.setRoles(user.getRoles());

        model.addAttribute("user", user);
        model.addAttribute("updateRequest", updateRequest);

        if (isAdmin) {
            List<Role> availableRoles = roleService.getAllRoles();
            model.addAttribute("availableRoles", availableRoles);
        }

        return "user/update";
    }

    @PostMapping("/{userId}/update")
    public String updateUser(@PathVariable Long userId,
                             @ModelAttribute UpdateUserRequest updateRequest,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userService.getUserById(userId));
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                model.addAttribute("availableRoles", roleService.getAllRoles());
            }
            return "user/update";
        }

        try {
            User updatedUser = userService.updateUser(updateRequest, userId);
            redirectAttributes.addFlashAttribute("message", "User updated successfully");
            return "redirect:/users/" + updatedUser.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/" + userId + "/update";
        }
    }

    @PostMapping("/{userId}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/users";
    }
}