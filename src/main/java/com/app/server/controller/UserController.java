package com.app.server.controller;

import com.app.server.dto.request.RoleChangeRequest;
import com.app.server.dto.request.UpdateUserRequestDto;
import com.app.server.dto.response.LoginResponseDto;
import com.app.server.model.User;
import com.app.server.service.JwtService;
import com.app.server.service.UserService;
import com.github.mfathi91.time.PersianDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    // Retrerieve all users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
            List<User> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Retrerieve by id
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
       User user = userService.findUserById(id);
       return new ResponseEntity<>(user, HttpStatus.OK);
    }



    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        Object res = userService.deleteUserById(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }



    // Update user
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(Authentication authentication,
                                            @PathVariable Long id,
                                            @RequestBody UpdateUserRequestDto user) {
        User changedUser = userService.updateUser(user,id);

        Authentication newAuth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(changedUser.getUsername(), user.getPassword())
        );


        String accessToken = jwtService.generateAccessToken(changedUser.getUsername(), authentication.getAuthorities());
        String refreshToken = jwtService.generateRefreshToken(changedUser.getUsername(), authentication.getAuthorities());


        LoginResponseDto response = LoginResponseDto.builder()
                .message("اطلاعات با موفقیت به روزرسانی شد")
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .timestamp(PersianDate.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // Retrerieve user roles
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @GetMapping("/{id}/role")
    public ResponseEntity<?> getUserRole(@PathVariable Long id) {
       User user = userService.findUserById(id);
       Set<String> roles = new HashSet<>();
        Set<String> authorities = new HashSet<>();

       user.getRoles().stream().forEach(role -> {
           roles.add(role.name().toString());
           role.getAuthorities().stream().forEach(authority -> {
               authorities.add(authority.name().toString());
           });
       });

       Map<String,Object> response = new HashMap<>();


       response.put("username",user.getUsername());
       response.put("roles",roles);
       response.put("authorities",authorities);


        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // Set user roles
    @PutMapping("/{id}/role")
    public ResponseEntity<?> changeUserRole(
            @PathVariable Long id,
            @RequestBody RoleChangeRequest request
    ) {
        User updatedUser = userService.changeUserRole(id, request.getRoles());
        return ResponseEntity.ok(updatedUser);
    }


}
