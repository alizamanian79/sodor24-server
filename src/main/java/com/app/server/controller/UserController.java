package com.app.server.controller;

import com.app.server.dto.request.RoleChangeRequest;
import com.app.server.dto.request.UpdateUserRequestDto;
import com.app.server.dto.response.LoginResponseDto;
import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.exception.AppBadRequestException;
import com.app.server.model.User;
import com.app.server.service.JwtService;
import com.app.server.service.UserService;
import com.github.mfathi91.time.PersianDate;
import jakarta.validation.Valid;
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

    // List
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
            List<User> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // get by id
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
       User user = userService.findUserById(id);
       return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // delete by id
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        Object res = userService.deleteUserById(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }



    // update user by id
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequestDto user) {

        try{

            User changedUser = userService.updateUser(user,id);
            String accessToken = jwtService.generateAccessToken(changedUser.getUsername(), authentication.getAuthorities());

            Map<String,Object>data=new HashMap<>();
            data.put("user",changedUser);
            data.put("access_token",accessToken);

            return Sodor24ResponseDto.response(
                    data,
                    "اطلاعات شما بروزرسانی شد",
                    "",
                    "",
                    HttpStatus.ACCEPTED
            );

        } catch (Exception e) {
            throw new AppBadRequestException(e.getMessage());
        }



    }

    // get user roles
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


        return Sodor24ResponseDto.response(response,"نقشهای کاربری" ,"","",HttpStatus.OK);
    }


    // set user roles
    @PutMapping("/{id}/role")
    public ResponseEntity<?> changeUserRole(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody RoleChangeRequest request
    ) {
        Map<String,Object> response = new HashMap<>();
        User updatedUser = userService.changeUserRole(id, request.getRoles());

        String accessToken = jwtService.generateAccessToken(updatedUser.getUsername(), authentication.getAuthorities());
        response.put("acccess_token",accessToken);
        response.put("roles",updatedUser.getRoles());
        return Sodor24ResponseDto.response(response,"نقش کاربر با موفقیت تخصیص داده شد","","",HttpStatus.OK);
    }


}
