package net.univwork.api.api_v1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "net.univwork.api.api_v1.repository.jpa")
public class JpaConfig {
}
