package com.app.server.service;

import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.UpdateUserRequestDto;
import com.app.server.dto.response.RegisterResponseDto;
import com.app.server.model.Role;
import com.app.server.model.User;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Set;

public interface UserService {

    List<User> getAllUsers();
    RegisterResponseDto registerUser(RegisterRequestDto req);
    User findUserByUsername(String username);
    Boolean existUserByUsername(String username);
    User updateUser(UpdateUserRequestDto req , Long id);
    User findUserById(Long id);

    User changeUserRole(Long id, Set<Role> roles);
    Object deleteUserById(Long id);
    User convertUserFromAuthentication(Authentication auth);
}
