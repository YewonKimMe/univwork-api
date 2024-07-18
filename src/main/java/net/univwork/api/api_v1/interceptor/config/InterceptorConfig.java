package net.univwork.api.api_v1.interceptor.config;

import lombok.RequiredArgsConstructor;
import net.univwork.api.api_v1.interceptor.UserCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final UserCheckInterceptor userCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(userCheckInterceptor)
//                .addPathPatterns("/**")
//                .order(1);
    }
}
