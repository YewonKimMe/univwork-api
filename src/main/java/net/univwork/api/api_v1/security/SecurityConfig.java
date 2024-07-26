package net.univwork.api.api_v1.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.enums.Role;
import net.univwork.api.api_v1.security.customfilter.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenGeneratorFilter jwtTokenGeneratorFilter;

    private final JwtTokenValidatorFilter jwtTokenValidatorFilter;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private final AuthorityLoggingFilterAfter authorityLoggingFilterAfter;

    private final CustomBasicAuthenticationEntryPoint basicAuthenticationEntryPoint;

    @Value("${cors.allowed-origin.dev}")
    private String devCorsAllowedUrl;

    @Value("${cors.allowed-origin.prod}")
    private String prodCorsAllowedUrl;
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf"); // csrf request 설정

        http.securityContext((contextConfigurer) ->
                contextConfigurer
                        .requireExplicitSave(false))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsCustomizer -> corsCustomizer.configurationSource(
                        new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration config = new CorsConfiguration();
                                config.setAllowedOrigins(Arrays.asList(
                                        devCorsAllowedUrl,
                                        prodCorsAllowedUrl
                                ));
                                config.setAllowedMethods(Collections.singletonList("*"));
                                config.setAllowCredentials(true);
                                config.setAllowedHeaders(Collections.singletonList("*"));
                                config.setExposedHeaders(List.of("Authorization")); // 클라이언트에서 Authorization header 에 접근 가능
                                config.setMaxAge(3600L);
                                return config;
                            }
                        }))
                .csrf((csrf) -> csrf.disable())
//                .csrf((csrf) -> csrf.csrfTokenRequestHandler(requestHandler)
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))// Cookie 로 csrf 토큰 설정, javascript 로 접근 가능
                .addFilterBefore(jwtTokenValidatorFilter, BasicAuthenticationFilter.class) // jwt validation
                //.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class) // csrf cookie
                .addFilterAfter(jwtTokenGeneratorFilter, BasicAuthenticationFilter.class) // jwt generation filter
                .addFilterAfter(authorityLoggingFilterAfter, BasicAuthenticationFilter.class) // logging, 인증 절차가 종료된 직후 바로 실행(로그인 성공);
                .authorizeHttpRequests((request) -> request // url path matcher
                        .requestMatchers("/api/v1/admin/**").hasRole(Role.ADMIN.getRole())
                        .requestMatchers("/api/v1/users/**").hasAnyRole(Role.USER.getRole(), Role.ADMIN.getRole())
                        .requestMatchers("/swagger-ui/**").hasRole(Role.ADMIN.getRole())
                        .requestMatchers("/api/v1/login/**").permitAll()
                        .requestMatchers("/api/v1/sign-up/**").permitAll()
                        .requestMatchers("/api/**", "/**").permitAll())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .httpBasic(configurer -> configurer
                        .authenticationEntryPoint(basicAuthenticationEntryPoint));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
