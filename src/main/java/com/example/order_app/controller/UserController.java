package com.example.order_app.controller;

import com.example.order_app.dto.UserDto;
import com.example.order_app.exception.AlreadyExistsException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.User;
import com.example.order_app.request.CreateUserRequest;
import com.example.order_app.request.UpdateUserRequest;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final IUserService userService;


    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        List<UserDto> userDtos = users.stream()
                .map(userService::convertUserToDto)
                .collect(Collectors.toList());
        model.addAttribute("users", userDtos);
        return "users/list";
    }

    @GetMapping("/{userId}")
    public String getUserById(@PathVariable Long userId, Model model) {
        try {
            User user = userService.getUserById(userId);
            UserDto userDto = userService.convertUserToDto(user);
            model.addAttribute("user", userDto);
            return "users/details";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/add")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new CreateUserRequest());
        return "users/add";
    }

    @PostMapping("/add")
    public String createUser(@ModelAttribute CreateUserRequest request, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.createUser(request);
            redirectAttributes.addFlashAttribute("message", "User created successfully");
            return "redirect:/users/" + user.getId();
        } catch (AlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/add";
        }
    }

    @GetMapping("/{userId}/update")
    public String showUpdateUserForm(@PathVariable Long userId, Model model) {
        try {
            User user = userService.getUserById(userId);
            UpdateUserRequest updateRequest = new UpdateUserRequest(); // You'll need to implement this
            // Copy relevant user details to updateRequest
            model.addAttribute("user", updateRequest);
            model.addAttribute("userId", userId);
            return "users/update";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/users";
        }
    }

    @PostMapping("/{userId}/update")
    public String updateUser(@PathVariable Long userId, @ModelAttribute UpdateUserRequest request, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.updateUser(request, userId);
            redirectAttributes.addFlashAttribute("message", "User updated successfully");
            return "redirect:/users/" + user.getId();
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users";
        }
    }

    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }
}