package com.example.order_app.config.oath2;

import com.example.order_app.model.User;
import com.example.order_app.repository.UserRepository;
import com.example.order_app.repository.RoleRepository;
import com.example.order_app.security.user.ShopUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;


@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String clientName = userRequest.getClientRegistration().getRegistrationId();

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(oauth2User, clientName);
        User user = processOAuth2User(customOAuth2User);

        return ShopUserDetails.buildUserDetails(user, oauth2User.getAttributes());
    }

    private User processOAuth2User(CustomOAuth2User oauth2User) {
        String email = oauth2User.getEmail();

        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = createNewUser(oauth2User);
        }

        return user;
    }

    private User createNewUser(CustomOAuth2User oauth2User) {
        User user = new User();
        user.setEmail(oauth2User.getEmail());
        user.setFirstName(oauth2User.getFirstName());
        user.setLastName(oauth2User.getLastName());
        user.setPassword(passwordEncoder.encode(oauth2User.getEmail())); // Use email as initial password
        user.setRoles(new HashSet<>(roleRepository.findByName("ROLE_USER")));

        return userRepository.save(user);
    }
}