//package com.example.order_app.service;
//
//import com.example.order_app.model.Admin;
//import com.example.order_app.model.Customer;
//import com.example.order_app.dto.UserRegistrationDto;
//import com.example.order_app.repository.AdminRepository;
//import com.example.order_app.repository.CustomerRepository;
//import com.example.order_app.repository.UserRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//@AllArgsConstructor
//public class UserService {
//
//    private final UserRepository userRepository;
//
//    private final CustomerRepository customerRepository;
//
//    private final AdminRepository adminRepository;
//
//    private final PasswordEncoder passwordEncoder;
//
//    public void registerUser(UserRegistrationDto userDto) {
//        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
//
//        if (userDto.getRole().equalsIgnoreCase("ADMIN")) {
//            Admin admin = new Admin();
//            admin.setUsername(userDto.getUsername());
//            admin.setPassword(encodedPassword);
//            admin.setEmail(userDto.getEmail());
//            admin.setCreatedAt(new java.util.Date());
//            admin.setUpdatedAt(new java.util.Date());
//            adminRepository.save(admin);
//        } else if (userDto.getRole().equalsIgnoreCase("CUSTOMER")) {
//            Customer customer = new Customer();
//            customer.setUsername(userDto.getUsername());
//            customer.setPassword(encodedPassword);
//            customer.setEmail(userDto.getEmail());
//            customer.setFirstName(userDto.getFirstName());
//            customer.setLastName(userDto.getLastName());
//            customer.setPhone(userDto.getPhone());
//            customer.setCity(userDto.getCity());
//            customer.setCountry(userDto.getCountry());
//            customer.setCreatedAt(new java.util.Date());
//            customer.setUpdatedAt(new java.util.Date());
//            customerRepository.save(customer);
//        }
//    }
//}
