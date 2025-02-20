package com.example.order_app.config.oath2;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {
    private final OAuth2User oauth2User;
    private final String clientName;

    public CustomOAuth2User(OAuth2User oauth2User, String clientName) {
        this.oauth2User = oauth2User;
        this.clientName = clientName;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oauth2User.getAttribute("name");
    }

    public String getEmail() {
        return oauth2User.getAttribute("email");
    }

    public String getFirstName() {
        if (clientName.equals("google")) {
            return oauth2User.getAttribute("given_name");
        } else if (clientName.equals("github")) {
            String name = oauth2User.getAttribute("name");
            return name != null ? name.split(" ")[0] : "";
        }
        return "";
    }

    public String getLastName() {
        if (clientName.equals("google")) {
            return oauth2User.getAttribute("family_name");
        } else if (clientName.equals("github")) {
            String name = oauth2User.getAttribute("name");
            String[] parts = name != null ? name.split(" ") : new String[]{""};
            return parts.length > 1 ? parts[1] : "";
        }
        return "";
    }
}