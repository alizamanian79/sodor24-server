package com.app.server.service.impliment;

import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.users.ChangeAccountOtpRequestDto;
import com.app.server.dto.request.users.VerifyAccountRequestDto;
import com.app.server.dto.response.MeResponseDto;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.User;
import com.app.server.repository.UserRepository;
import com.app.server.service.UserService;
import com.app.server.util.ExternalRequest.ExternalRequest;
import com.app.server.util.ExternalRequest.dto.ExternalRequestDto;
import com.app.server.util.ExternalRequest.dto.Method;
import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final WalletRMQProducer walletRMQProducer;

    private final ExternalRequest externalRequest;

    @Value("${auth-service.base-url:http://localhost:8081}")
    private String baseUrl;


    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }


    @Override
    public User findUserBySub(String sub) {
        return userRepository.findUserBySub(sub).orElseThrow(()->new AppNotFoundException("کاربرا پیدا نشد"));
    }

    @Override
    public User createUser(String sub) {
        User createUser=User.builder()
                .sub(sub)
                .walletId(sub)
                .build();
       return userRepository.save(createUser);
    }


    @Override
    public MeResponseDto me(String accessToken) {

        Map<String, Object> accountData = new HashMap<>();

        ExternalRequestDto req = ExternalRequestDto.builder()
                .url(baseUrl + "/api/v1/users/me")
                .token(accessToken)
                .body(null)
                .method(Method.GET)
                .param(null)
                .build();

        accountData = externalRequest.sendRequest(req);
        WalletResponseDto walletData = walletRMQProducer.getWalletBySub(accountData.get("sub").toString());

        User serviceData =
                findUserBySub(accountData.get("sub").toString());

        return MeResponseDto.builder()
                .sub(accountData.get("sub").toString())
                .accountData(accountData)
                .walletData(walletData.equals(null)? "سرویس در دسترس نیست و یا خاموش میباشد" :walletData.getData())
                .serviceData(serviceData)
                .build();
    }

}