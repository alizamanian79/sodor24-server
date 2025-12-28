package com.app.server.filter;

import com.app.server.model.User;
import com.app.server.service.JwtService;
import com.app.server.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAccessTokenQueryParamFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = request.getParameter("token");

        if (token != null && !token.isBlank()) {

            Claims claims = jwtService.claimsFromToken(token);
            if (claims != null && "access-token".equals(claims.get("type"))) {

                String username = claims.getSubject();
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    User user = userService.findUserByUsername(username);
                    if (user != null) {

                        List<String> authorityStrings = claims.get("authorities", List.class);
                        var authorities = authorityStrings == null
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


                        request = new HttpServletRequestWrapper(request) {
                            @Override
                            public String getHeader(String name) {
                                if ("Authorization".equalsIgnoreCase(name)) {
                                    return "Bearer " + token;
                                }
                                return super.getHeader(name);
                            }
                        };
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
