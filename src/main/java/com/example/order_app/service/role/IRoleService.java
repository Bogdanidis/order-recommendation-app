package com.example.order_app.service.role;


import com.example.order_app.model.Role;

import java.util.List;
import java.util.Set;

public interface IRoleService {
    List<Role> getAllRoles();

    Set<Role> findByName(String roleUser);
}
