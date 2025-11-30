package com.app.server.filter;

import com.app.server.model.User;
import com.app.server.service.JwtService;
import com.app.server.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
@RequiredArgsConstructor
public class JwtAccessTokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.substring(7);


        Claims claims = jwtService.claimsFromToken(token);
        if (claims == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String type = (String) claims.get("type");
        if (!"access-token".equals(type)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = claims.getSubject();
        if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }


        User user = userService.findUserByUsername(username);
        if (user == null) {
            filterChain.doFilter(request, response);
            return;
        }

        List<String> authorityStrings = claims.get("authorities", List.class);
        Collection<SimpleGrantedAuthority> authorities = authorityStrings == null
                ? user.getAuthorities().stream()
                .map(a -> new SimpleGrantedAuthority(a.getAuthority()))
                .collect(Collectors.toList())
                : authorityStrings.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                authorities
        );



        ((UsernamePasswordAuthenticationToken) authentication)
                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        List<String> excludePaths = List.of(
                "/api/v1/auth",
                "/api/v1/public"
        );

        return excludePaths.contains(request.getServletPath());
    }
}
