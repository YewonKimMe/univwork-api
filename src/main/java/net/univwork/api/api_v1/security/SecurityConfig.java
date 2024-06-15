package net.univwork.api.api_v1.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/admin/**").authenticated()
                )
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/**").permitAll()
                )
                .formLogin(
                        Customizer.withDefaults()
                )
                .sessionManagement(
                        sessionManagementConfig -> sessionManagementConfig
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(false)
                )
                .csrf(
                        csrf -> csrf.disable()
                )
                .httpBasic(
                        Customizer.withDefaults()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
