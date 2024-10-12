package com.example.order_app.controller;

import com.example.order_app.dto.UserDto;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Role;
import com.example.order_app.model.User;
import com.example.order_app.request.AddUserRequest;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final IUserService userService;
    private final IRoleService roleService;

    /**
     * Displays a list of all users (admin only).
     *
     * @param page Page number
     * @param size Number of items per page
     * @param search Search term
     * @param model Spring MVC Model
     * @return The name of the user list view
     */
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

    /**
     * Displays details of a specific user.
     *
     * @param userId ID of the user
     * @param model Spring MVC Model
     * @param authentication Spring Security Authentication object
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return The name of the user details view or a redirect URL
     */
    @GetMapping("/{userId}")
    public String getUserDetails(@PathVariable Long userId,
                                 Model model,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(userId);

            boolean isOwnProfile = authentication != null &&
                    authentication.getName().equals(user.getEmail());
            boolean isAdmin = authentication != null &&
                    authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isOwnProfile && !isAdmin) {
                redirectAttributes.addFlashAttribute("error",
                        "You don't have permission to view this user profile.");
                return "redirect:/home";
            }

            model.addAttribute("user", user);
            model.addAttribute("isOwnProfile", isOwnProfile);
            return "user/details";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/home";
        }
    }

    /**
     * Displays the form to add a new user (admin only).
     *
     * @param model Spring MVC Model
     * @return The name of the add user view
     */
    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showAddUserForm(Model model) {
        model.addAttribute("addUserRequest", new AddUserRequest());
        List<Role> availableRoles = roleService.getAllRoles();
        model.addAttribute("availableRoles", availableRoles);
        return "user/add";
    }

    /**
     * Handles the submission of a new user (admin only).
     *
     * @param addUserRequest User creation data
     * @param bindingResult BindingResult for form validation
     * @param model Spring MVC Model
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addUser(@Valid @ModelAttribute AddUserRequest addUserRequest,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<Role> availableRoles = roleService.getAllRoles();
            model.addAttribute("availableRoles", availableRoles);
            return "user/add";
        }

        try {
            User createdUser = userService.addUser(addUserRequest);
            redirectAttributes.addFlashAttribute("success", "User created successfully");
            return "redirect:/users/" + createdUser.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/add";
        }
    }

    /**
     * Displays the form to update an existing user.
     *
     * @param userId ID of the user to update
     * @param model Spring MVC Model
     * @param authentication Spring Security Authentication object
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return The name of the update user view or a redirect URL
     */
    @GetMapping("/{userId}/update")
    public String showUpdateUserForm(@PathVariable Long userId,
                                     Model model,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(userId);
            boolean isOwnProfile = authentication.getName().equals(user.getEmail());
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isOwnProfile && !isAdmin) {
                redirectAttributes.addFlashAttribute("error",
                        "You don't have permission to edit this user profile.");
                return "redirect:/home";
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
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/home";
        }
    }

    /**
     * Handles the submission of an updated user.
     *
     * @param userId ID of the user to update
     * @param updateRequest The updated user data
     * @param bindingResult BindingResult for form validation
     * @param model Spring MVC Model
     * @param redirectAttributes RedirectAttributes for flash messages
     * @param authentication Spring Security Authentication object
     * @return Redirect URL
     */
    @PutMapping("/{userId}/update")
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
            redirectAttributes.addFlashAttribute("success", "User updated successfully");
            return "redirect:/users/" + updatedUser.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/" + userId + "/update";
        }
    }

    /**
     * Handles the deletion of a user (admin only).
     *
     * @param userId ID of the user to delete
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @DeleteMapping("/{userId}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/users";
    }
}