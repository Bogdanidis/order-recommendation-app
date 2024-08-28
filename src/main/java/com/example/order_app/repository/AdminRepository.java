package com.example.order_app.repository;

import com.example.order_app.model.Admin;
import com.example.order_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);

}
