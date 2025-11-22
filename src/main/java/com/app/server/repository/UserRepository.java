package com.app.server.repository;


import com.app.server.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserById(Long id);


    boolean existsByPhoneNumber(@NotBlank(message = "شماره تماس نمی‌تواند خالی باشد") @Pattern(regexp = "\\d{11}", message = "شماره تماس باید دقیقا 11 رقم باشد (0912xxxxxxx)") String phoneNumber);

    boolean existsByUsername(String username);
}
