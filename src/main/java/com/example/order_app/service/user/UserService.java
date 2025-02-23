package com.example.order_app.service.user;

import com.example.order_app.dto.UserDto;
import com.example.order_app.exception.AlreadyExistsException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.exception.UnauthorizedOperationException;
import com.example.order_app.model.Role;
import com.example.order_app.model.User;
import com.example.order_app.repository.RoleRepository;
import com.example.order_app.repository.UserRepository;
import com.example.order_app.request.AddUserRequest;
import com.example.order_app.request.UpdateUserRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


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

    @Cacheable(value = "users", key = "#userId")
    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
    }

    @CacheEvict(value = "users", allEntries = true)
    @Override
    @Transactional
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

//                    // Fetch fresh roles within transaction
//                    Set<Role> roles = request.getRoles().stream()
//                            .map(role -> roleRepository.findByName(role.getName()))
//                            .flatMap(Set::stream)
//                            .collect(Collectors.toSet());
//                    user.setRoles(roles);

                    return  userRepository.save(user);
                }) .orElseThrow(() -> new AlreadyExistsException("Oops!" +request.getEmail() +" already exists!"));
    }

    @CacheEvict(value = "users", allEntries = true)
    @Override
    @Transactional
    public User updateUser(UpdateUserRequest request, Long userId) {
        return  userRepository.findById(userId).map(existingUser ->{
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            existingUser.setRoles(request.getRoles());
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found!"));

    }

    @CacheEvict(value = "users", allEntries = true)
    @Override
    @Transactional
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
            userRepository.findById(userId)
                .ifPresentOrElse(
                        product -> userRepository.softDelete(userId, LocalDateTime.now()),
                        () -> { throw new ResourceNotFoundException("User not found!"); }
                );
        }
    }

    @Override
    public Long countUsers() {
        return userRepository.count();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
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
