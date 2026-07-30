package com.app.server.annotation;

import com.app.server.dto.request.LoginRequestDto;
import com.app.server.exception.AppBadRequestException;
import com.app.server.exception.AppForbiddenException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.User;
import com.app.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class VerifiedUserAspect {

    private final UserRepository userRepository;

    @Before("@annotation(com.app.server.annotation.VerifiedUser)")
    public void checkUser(JoinPoint joinPoint) {

        for (Object arg : joinPoint.getArgs()) {

            if (arg instanceof LoginRequestDto loginRequest) {

                String username = loginRequest.getUsername();

                User user = userRepository.findUserByUsername(username)
                        .orElseThrow(() -> new AppNotFoundException("کاربر یافت نشد."));

                if (!user.isValid()) {
                    throw new AppForbiddenException(
                            "احراز هویت شما تایید نشده لطفا کد داده شده را وارد نمایید",
                            "",
                            "");
                }

                return;
            }
        }

        throw new AppBadRequestException("شماره تماس ارسال نشده است.");
    }


//    @Before("@annotation(verifiedUser)")
//    public void verifiedUserValid(VerifiedUser verifiedUser) {
//
//        String phoneNumber = verifiedUser.phoneNumber();
//
//        User user = userRepository.findUserByPhoneNumber(phoneNumber)
//                .orElseThrow(() -> new AppBadRequestException("کاربر یافت نشد."));
//
//        if (!user.isValid()) {
//            throw new AppBadRequestException("حساب کاربری تایید نشده است.");
//        }
//    }


//    @Before("@annotation(com.app.server.annotation.VerifiedUser)")
//    public void VerifiedUserValid() {
//
//        Authentication authentication =
//                SecurityContextHolder.getContext().getAuthentication();
//
//        User principal = (User) authentication.getPrincipal();
//
//
//        User user = userRepository.findUserByPhoneNumber()
//                .orElseThrow(() -> new AppBadRequestException("کاربر یافت نشد."));
//
//        if (!user.isValid()) {
//            throw new AppBadRequestException("حساب کاربری شما تایید نشده است.");
//        }
//    }
}