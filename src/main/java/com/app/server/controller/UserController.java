package com.app.server.controller;

import com.app.server.dto.request.RoleChangeRequest;
import com.app.server.dto.request.UpdateUserRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.model.User;
import com.app.server.service.JwtService;
import com.app.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Retrieve all users. Admin only.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieve a single user by id. Admin, or the user themselves.
     */
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Delete a user by id. Admin only.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        Object res = userService.deleteUserById(id);
        return ResponseEntity.ok(res);
    }

    /**
     * Update a user's profile. Admin, or the user themselves.
     * Re-issues fresh access/refresh tokens since the username embedded in
     * the old tokens may no longer be valid after the update.
     */
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @PutMapping("/{id}")
    public ResponseEntity<CustomResponseDto> updateUser(Authentication authentication,
                                                        @PathVariable Long id,
                                                        @Valid @RequestBody UpdateUserRequestDto user) {
        User changedUser = userService.updateUser(user, id);

        String accessToken = jwtService.generateAccessToken(changedUser.getUsername(), authentication.getAuthorities());
        String refreshToken = jwtService.generateRefreshToken(changedUser.getUsername(), authentication.getAuthorities());

        Map<String, Object> data = new HashMap<>();
        data.put("access_token", accessToken);
        data.put("refresh_token", refreshToken);
        data.put("user", changedUser);

        CustomResponseDto result = CustomResponseDto.builder()
                .data(data)
                .message("اطلاعات شما به روز رسانی شد")
                .status(HttpStatus.OK.value())
                .details(null)
                .build();

        return ResponseEntity.ok(result);
    }

    /**
     * Retrieve a user's roles and their flattened authorities.
     * Admin, or the user themselves.
     */
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @GetMapping("/{id}/role")
    public ResponseEntity<Map<String, Object>> getUserRole(@PathVariable Long id) {
        User user = userService.findUserById(id);

        Set<String> roles = new HashSet<>();
        Set<String> authorities = new HashSet<>();

        user.getRoles().forEach(role -> {
            roles.add(role.name());
            role.getAuthorities().forEach(authority -> authorities.add(authority.name()));
        });

        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("roles", roles);
        response.put("authorities", authorities);

        return ResponseEntity.ok(response);
    }

    /**
     * Change a user's roles. Admin only.
     * IMPORTANT: this must stay ADMIN-only (no self-access), otherwise any
     * authenticated user could grant themselves elevated roles. The previous
     * version had NO @PreAuthorize at all, which was a privilege-escalation
     * vulnerability.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public ResponseEntity<CustomResponseDto> changeUserRole(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody RoleChangeRequest request
    ) {
        User existUser = userService.changeUserRole(id, request.getRoles());
        String accessToken = jwtService.generateAccessToken(existUser.getUsername(), authentication.getAuthorities());

        Map<String, Object> data = new HashMap<>();
        data.put("access_token", accessToken);
        data.put("roles", existUser.getRoles());

        CustomResponseDto result = CustomResponseDto.builder()
                .data(data)
                .message("نقش کاربر تخصیص داده شد")
                .status(HttpStatus.OK.value())
                .details(null)
                .build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}