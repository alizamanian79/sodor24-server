package com.app.server.service.impliment;

import com.app.server.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.access-token.expired-time.minutes}")
    private int accessTokenExpiredTime;

    @Value("${jwt.refresh-token.expired-time.day}")
    private int refreshTokenExpiredTime;

    @Value("${jwt.access-token-secret}")
    private String accessTokenSecret;

    @Value("${jwt.refresh-token-secret}")
    private String refreshTokenSecret;

    private Key accessTokenKey;
    private Key refreshTokenKey;

    private long ACCESS_TOKEN_EXP;
    private long REFRESH_TOKEN_EXP;

    @PostConstruct
    public void init() {
        accessTokenKey = Keys.hmacShaKeyFor(accessTokenSecret.getBytes());
        refreshTokenKey = Keys.hmacShaKeyFor(refreshTokenSecret.getBytes());

        ACCESS_TOKEN_EXP = accessTokenExpiredTime * 60L * 1000L;
        REFRESH_TOKEN_EXP = refreshTokenExpiredTime * 24L * 60L * 60L * 1000L;
    }

    @Override
    public String generateAccessToken(String subject, Collection<? extends GrantedAuthority> authorities) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("type", "access-token")
                .claim("authorities", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP))
                .signWith(accessTokenKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Claims claimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(accessTokenKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            return null;
        }
    }

    @Override
    public String parseAccessToken(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(accessTokenKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    @Override
    public String generateRefreshToken(String subject, Collection<? extends GrantedAuthority> authorities) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("type", "refresh-token")
                .claim("authorities", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXP))
                .signWith(refreshTokenKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String convertRefreshTokenToAccessToken(String refreshToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(refreshTokenKey)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            if (!"refresh-token".equals(claims.get("type"))) {
                throw new IllegalArgumentException("Invalid refresh token type");
            }

            String subject = claims.getSubject();
            @SuppressWarnings("unchecked")
            List<String> authoritiesList = claims.get("authorities", List.class);
            List<GrantedAuthority> authorities = authoritiesList.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return generateAccessToken(subject, authorities);

        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid or expired refresh token", e);
        }
    }


    @Override
    public String generateCallbackAccessToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("type", "callback-access-token")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (5 * 60 * 1000))) // 5 دقیقه
                .signWith(accessTokenKey, SignatureAlgorithm.HS256)
                .compact();
    }

}
