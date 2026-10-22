package com.app.server.service.impliment;

import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.users.ChangeAccountOtpRequestDto;
import com.app.server.dto.request.users.VerifyAccountRequestDto;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.User;
import com.app.server.repository.UserRepository;
import com.app.server.service.UserService;
import com.app.server.util.ExternalRequest.ExternalRequest;
import com.app.server.util.ExternalRequest.dto.ExternalRequestDto;
import com.app.server.util.ExternalRequest.dto.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository usreRepository;
    private final ExternalRequest externalRequest;

    @Override
    public List<User> getAllUsers() {
        return usreRepository.findAll();
    }





    @Override
    public User findUserBySub(String sub) {
        return usreRepository.findUserBySub(sub).orElseThrow(()->new AppNotFoundException("کاربرا پیدا نشد"));
    }







}