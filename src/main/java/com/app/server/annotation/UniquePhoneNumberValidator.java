package com.app.server.annotation;


import com.app.server.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniquePhoneNumberValidator implements ConstraintValidator<UniquePhoneNumber, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        boolean exist = userRepository.existsByPhoneNumber(phoneNumber);
        if (exist) {
            return false;
        }
        return true;
    }
}
