package com.example.order_app.service;

import com.example.order_app.model.Admin;
import com.example.order_app.model.Customer;
import com.example.order_app.model.UserRegistrationDto;
import com.example.order_app.repository.AdminRepository;
import com.example.order_app.repository.CustomerRepository;
import com.example.order_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(UserRegistrationDto userDto) {
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        if (userDto.getRole().equalsIgnoreCase("ADMIN")) {
            Admin admin = new Admin();
            admin.setUsername(userDto.getUsername());
            admin.setPassword(encodedPassword);
            admin.setEmail(userDto.getEmail());
            adminRepository.save(admin);
        } else if (userDto.getRole().equalsIgnoreCase("CUSTOMER")) {
            Customer customer = new Customer();
            customer.setUsername(userDto.getUsername());
            customer.setPassword(encodedPassword);
            customer.setEmail(userDto.getEmail());
            customer.setFirst_name(userDto.getFirstName());
            customer.setLast_name(userDto.getLastName());
            customer.setPhone(userDto.getPhone());
            customer.setCity(userDto.getCity());
            customer.setCountry(userDto.getCountry());
            customerRepository.save(customer);
        }
    }
}
