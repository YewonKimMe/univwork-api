package net.univwork.api.api_v1.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.enums.Role;
import net.univwork.api.api_v1.security.customfilter.AuthorityLoggingFilterAfter;
import net.univwork.api.api_v1.security.customfilter.JwtTokenGeneratorFilter;
import net.univwork.api.api_v1.security.customfilter.JwtTokenValidatorFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
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
                                config.setAllowedOrigins(Collections.singletonList("http://localhost:8080"));
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
//                        // Cookie 로 csrf 토큰 설정, javascript 로 접근 가능
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                // jwt validation
                .addFilterBefore(new JwtTokenValidatorFilter(), BasicAuthenticationFilter.class)
                // csrf cookie
                //.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                // jwt generation
                .addFilterAfter(new JwtTokenGeneratorFilter(), BasicAuthenticationFilter.class)
                // logging, 인증 절차가 종료된 직후 바로 실행(로그인 성공);
                .addFilterAfter(new AuthorityLoggingFilterAfter(), BasicAuthenticationFilter.class)
                // url path matcher
                .authorizeHttpRequests((request) -> request
                        .requestMatchers("/api/v1/admin/**").hasRole(Role.ADMIN.getRole())
                        .requestMatchers("/api/v1/user/**").hasAnyRole(Role.USER.getRole(), Role.ADMIN.getRole())
                        .requestMatchers("/api/v1/login/**").permitAll()
                        .requestMatchers("/api/v1/sign-up/**").permitAll()
                        .requestMatchers("/api/**", "/**").permitAll())
                //.formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
