package com.example.order_app.repository;

import com.example.order_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    User findByEmail(String email);

    Page<User> findByFirstNameContainingOrLastNameContainingOrEmailContaining(String firstName, String lastName, String email, Pageable pageable);}
