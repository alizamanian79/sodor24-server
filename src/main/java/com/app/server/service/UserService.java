package com.app.server.service;

import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.UpdateUserRequestDto;
import com.app.server.dto.request.users.ChangeAccountOtpRequestDto;
import com.app.server.dto.request.users.VerifyAccountRequestDto;
import com.app.server.dto.response.RegisterResponseDto;
import com.app.server.model.User;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<User> getAllUsers();
    User findUserBySub(String sub);
}
