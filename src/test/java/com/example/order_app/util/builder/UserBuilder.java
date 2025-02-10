package com.example.order_app.util.builder;

import com.example.order_app.model.Role;
import com.example.order_app.model.User;
import java.util.HashSet;
import java.util.Set;

public class UserBuilder {
    private final User user;

    public UserBuilder() {
        user = new User();
    }

    public static UserBuilder aUser() {
        return new UserBuilder();
    }

    public UserBuilder withEmail(String email) {
        user.setEmail(email);
        return this;
    }

    public UserBuilder withPassword(String password) {
        user.setPassword(password);
        return this;
    }

    public UserBuilder withFirstName(String firstName) {
        user.setFirstName(firstName);
        return this;
    }

    public UserBuilder withLastName(String lastName) {
        user.setLastName(lastName);
        return this;
    }

    public UserBuilder withRoles(Set<Role> roles) {
        user.setRoles(roles);
        return this;
    }

    public User build() {
        return user;
    }
}