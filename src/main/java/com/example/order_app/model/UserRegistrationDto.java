package com.example.order_app.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDto {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String role; // "ADMIN" or "CUSTOMER"


    // Additional fields for Customer

    private String firstName;
    private String lastName;
    private String phone;
    private String city;
    private String country;


}