package com.app.server.config;


import com.app.server.exception.CustomAccessDeniedHandler;
import com.app.server.exception.CustomAuthEntryPoint;
import com.app.server.filter.JwtAccessTokenFilter;
import com.app.server.filter.JwtAccessTokenQueryParamFilter;
import com.app.server.filter.JwtRefreshTokenFilter;
import com.app.server.filter.RequestInfoFilter;
import com.app.server.provider.UsernamePasswordProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Value("${app.client.host}")
    private String clientHost;


    private final  UsernamePasswordProvider usernamePasswordProvider;
    private final JwtAccessTokenFilter jwtAccessTokenFilter;
    private final JwtRefreshTokenFilter jwtRefreshTokenFilter;
    private final CustomAuthEntryPoint unauthorizedHandler;
    private final RequestInfoFilter requestInfoFilter;
    private final JwtAccessTokenQueryParamFilter jwtAccessTokenQueryParamFilter;

    public SecurityConfig(@Lazy UsernamePasswordProvider usernamePasswordProvider,
                          @Lazy JwtAccessTokenFilter jwtAccessTokenFilter,
                          @Lazy JwtRefreshTokenFilter jwtRefreshTokenFilter,
                          @Lazy CustomAuthEntryPoint unauthorizedHandler,
                          @Lazy RequestInfoFilter requestInfoFilter,
                          @Lazy JwtAccessTokenQueryParamFilter jwtAccessTokenQueryParamFilter
    ) {
        this.usernamePasswordProvider = usernamePasswordProvider;
        this.jwtRefreshTokenFilter = jwtRefreshTokenFilter;
        this.jwtAccessTokenFilter = jwtAccessTokenFilter;
        this.unauthorizedHandler = unauthorizedHandler;
        this.requestInfoFilter = requestInfoFilter;
        this.jwtAccessTokenQueryParamFilter = jwtAccessTokenQueryParamFilter;
    }


    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Arrays.asList(usernamePasswordProvider));
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors->cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(ses -> ses.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            "/api/v1/auth/**",
                            "/api/v1/public/**",
                            "/api/v1/payment/callback/**",
                            "/h2-console/**"
                    ).permitAll();
                    auth.anyRequest().authenticated();
                })

                .addFilterBefore(jwtAccessTokenQueryParamFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAccessTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtRefreshTokenFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(ex->{
                    ex.authenticationEntryPoint(unauthorizedHandler);
                    ex.accessDeniedHandler(new CustomAccessDeniedHandler());
                })

        ;


        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 1 << 12, 3);
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(clientHost,"http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public FilterRegistrationBean<RequestInfoFilter> requestInfoFilterRegistration() {
        FilterRegistrationBean<RequestInfoFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(requestInfoFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }


}
