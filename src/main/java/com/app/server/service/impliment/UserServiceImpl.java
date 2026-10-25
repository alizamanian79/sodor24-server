package com.app.server.service.impliment;

import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.users.ChangeAccountOtpRequestDto;
import com.app.server.dto.request.users.VerifyAccountRequestDto;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.User;
import com.app.server.repository.UserRepository;
import com.app.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }



    @Override
    public User findUserBySub(String sub) {
        return userRepository.findUserBySub(sub).orElseThrow(()->new AppNotFoundException("کاربرا پیدا نشد"));
    }

    @Override
    public User createUser(RegisterRequestDto req, String sub) {
        User createUser=User.builder()
                .sub(sub)
                .isValid(true)
                .phoneNumber(req.getPhoneNumber())
                .username(req.getUsername())
                .walletId(null)
                .nationalCode(req.getNationalCode())
                .build();
       return userRepository.save(createUser);
    }


}