package com.app.server.annotation;

import com.app.server.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueUserNameValidator implements ConstraintValidator<UniqueUserName, String> {

    @Autowired
    public UserRepository userRepository;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {

        boolean exists = userRepository.existsByUsername(username);
        if (exists) {
            return false;
        }
        return true;

    }
}
