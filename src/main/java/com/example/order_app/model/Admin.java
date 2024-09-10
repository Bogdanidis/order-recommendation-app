package com.example.order_app.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "id")
public class Admin extends User {

    public Admin() {
        super();
        this.setRole("ADMIN");
    }

    public Admin(String username, String password, String email) {
        super(username, password, email);
        this.setRole("ADMIN");
    }
}

