package com.example.order_app.service.user;

import com.example.order_app.dto.UserDto;
import com.example.order_app.exception.AlreadyExistsException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.exception.UnauthorizedOperationException;
import com.example.order_app.model.User;
import com.example.order_app.repository.RoleRepository;
import com.example.order_app.repository.UserRepository;
import com.example.order_app.request.AddUserRequest;
import com.example.order_app.request.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Page<UserDto> findAllPaginated(Pageable pageable, String search) {
        Page<User> userPage;
        if (search != null && !search.isEmpty()) {
            userPage = userRepository.findByFirstNameContainingOrLastNameContainingOrEmailContaining(search, search, search, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        return userPage.map(this::convertUserToDto);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
    }

    @Override
    public User addUser(AddUserRequest request) {
        return  Optional.of(request)
                .filter(user -> !userRepository.existsByEmail(request.getEmail()))
                .map(req -> {
                    User user = new User();
                    user.setEmail(request.getEmail());
                    user.setPassword(passwordEncoder.encode(request.getPassword()));
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    user.setRoles(request.getRoles());
                    return  userRepository.save(user);
                }) .orElseThrow(() -> new AlreadyExistsException("Oops!" +request.getEmail() +" already exists!"));
    }

    @Override
    public User updateUser(UpdateUserRequest request, Long userId) {
        return  userRepository.findById(userId).map(existingUser ->{
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            existingUser.setRoles(request.getRoles());
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found!"));

    }

    @Override
    public void deleteUser(Long userId) {
        User currentUser = getAuthenticatedUser();
        User userToDelete = getUserById(userId);

        // Check if the user to be deleted is an admin
        boolean isUserToDeleteAdmin = userToDelete.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));


        // Check if an admin is trying to delete another admin
        if (isUserToDeleteAdmin) {
            throw new UnauthorizedOperationException("Admins cannot delete other admin accounts.");
        }else{
            userRepository.findById(userId).ifPresentOrElse(userRepository :: delete, () ->{
                throw new ResourceNotFoundException("User not found!");
            });
        }
    }

    @Override
    public Long countUsers() {
        return userRepository.count();
    }

    @Override
    public UserDto convertUserToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }

}
