package com.example.order_app.service.user;

import com.example.order_app.dto.UserDto;
import com.example.order_app.model.User;
import com.example.order_app.request.CreateUserRequest;
import com.example.order_app.request.UpdateUserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    List<User> findAll();
    Page<UserDto> findAllPaginated(Pageable pageable, String search);

    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(UpdateUserRequest request, Long userId);
    void deleteUser(Long userId);

    UserDto convertUserToDto(User user);

    User getAuthenticatedUser();

    Long countUsers();

}
