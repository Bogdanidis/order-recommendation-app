package com.example.order_app.service.auth;

import com.example.order_app.exception.AlreadyExistsException;
import com.example.order_app.model.User;
import com.example.order_app.repository.UserRepository;
import com.example.order_app.request.AddUserRequest;
import com.example.order_app.service.user.UserService;
import com.example.order_app.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private AddUserRequest addUserRequest;

    @BeforeEach
    void setUp() {
        testUser = TestDataUtil.createTestUser();
        addUserRequest = new AddUserRequest();
        // Setup addUserRequest with test data
        addUserRequest.setEmail(testUser.getEmail());
        addUserRequest.setPassword("password");
        addUserRequest.setFirstName(testUser.getFirstName());
        addUserRequest.setLastName(testUser.getLastName());
        addUserRequest.setRoles(testUser.getRoles());
    }

    @Test
    void shouldAddUser() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.addUser(addUserRequest);

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> userService.addUser(addUserRequest));
    }
}
