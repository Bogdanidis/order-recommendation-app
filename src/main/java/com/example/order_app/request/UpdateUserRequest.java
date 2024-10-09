package com.example.order_app.request;

import com.example.order_app.model.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Collection;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private Collection<Role> roles;
}
