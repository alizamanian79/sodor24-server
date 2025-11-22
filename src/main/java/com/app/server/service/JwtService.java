package com.app.server.service;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public interface JwtService {
    String generateAccessToken(String subject, Collection<? extends GrantedAuthority> authorities);
    String generateRefreshToken(String subject,Collection<? extends GrantedAuthority> authorities);
    String parseAccessToken(String accessToken);
    String convertRefreshTokenToAccessToken(String refreshToken);

     Claims claimsFromToken(String accessToken);

}
