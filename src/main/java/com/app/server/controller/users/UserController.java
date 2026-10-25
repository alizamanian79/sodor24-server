package com.app.server.controller.users;

import com.app.server.model.User;
import com.app.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // List
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
            List<User> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
    }

//
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getUserById(@PathVariable Long id) {
//       User user = userService.findUserById(id);
//       return new ResponseEntity<>(user, HttpStatus.OK);
//    }
//
//    // delete by id
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
//        Object res = userService.deleteUserById(id);
//
//       CustomResponseDto response= CustomResponseDto.builder()
//                .data(res)
//                .message("user deleted")
//                .status(200)
//                .build();
//        return ResponseEntity.status(response.getStatus()).body(response);
//    }



//
//
//    @IsSelfOrAdminBySub
//    @PutMapping("/{sub}/{id}")
//    public ResponseEntity<?> updateUser(
//            @PathVariable String sub,
//            @PathVariable Long id,
//            @AuthenticationPrincipal Jwt jwt,
//            @Valid @RequestBody UpdateUserRequestDto req) {
//
//        try {
//
//
//            User changedUser = userService.updateUser(req, id);
//            req.setSub(changedUser.getSub());
//            req.setUsername(changedUser.getUsername());
//             Map<String,Object> res= authenticationService.updateUser(req, jwt.getTokenValue());
//
//
//             CustomResponseDto response =
//                     CustomResponseDto.builder()
//                             .data(res)
//                             .message("updated user info successfully")
//                             .build();
//
//            return ResponseEntity.status(response.getStatus()).body(response);
//        } catch (Exception e) {
//            throw new AppBadRequestException(e.getMessage());
//        }
//    }






    // get user roles
//    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
//    @GetMapping("/{id}/role")
//    public ResponseEntity<?> getUserRole(@PathVariable Long id) {
//       User user = userService.findUserById(id);
//       Set<String> roles = new HashSet<>();
//        Set<String> authorities = new HashSet<>();
//
//       user.getRoles().stream().forEach(role -> {
//           roles.add(role.name().toString());
//           role.getAuthorities().stream().forEach(authority -> {
//               authorities.add(authority.name().toString());
//           });
//       });
//
//       Map<String,Object> response = new HashMap<>();
//
//
//
//
//
//        response.put("username",user.getUsername());
//       response.put("roles",roles);
//       response.put("authorities",authorities);
//
//
//        return Sodor24ResponseDto.response(response,"نقشهای کاربر با موفقیت فراخوانی شد" ,"","",HttpStatus.OK);
//    }


    // set user roles
//    @PutMapping("/{id}/role")
//    public ResponseEntity<?> changeUserRole(
//            Authentication authentication,
//            @PathVariable Long id,
//            @Valid @RequestBody RoleChangeRequest request
//    ) {
//        Map<String,Object> response = new HashMap<>();
//        User updatedUser = userService.changeUserRole(id, request.getRoles());
//
//        response.put("roles",updatedUser.getRoles());
//        return Sodor24ResponseDto.response(response,"نقشهای کاربر با موفقیت تخصیص داده شدند","","",HttpStatus.OK);
//    }


}
