package com.app.server.filter;

import com.app.server.model.User;
import com.app.server.service.UserService;
import com.app.server.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Order(2)
@Component
@RequiredArgsConstructor
public class JwtRefreshTokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String refreshToken = request.getHeader("Authorization");

        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
            try {
                String newAccessToken = jwtService.convertRefreshTokenToAccessToken(refreshToken);
                response.setHeader("Authorization", "Bearer " + newAccessToken);
                response.setStatus(HttpServletResponse.SC_OK);
                request.setAttribute("NEW_ACCESS_TOKEN", newAccessToken);

                String username = jwtService.parseAccessToken(newAccessToken);
                User user  = userService.findUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);



            } catch (Exception e) {

                filterChain.doFilter(request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/api/v1/auth");
    }
}
