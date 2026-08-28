package com.app.server.annotation;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@userSecurity.isSelfOrAdminBySub(authentication, #sub)")
public @interface IsSelfOrAdminBySub {
}