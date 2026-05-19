package roomescape.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthenticationConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns(
                        "/api/v1/reservations/**",
                        "/api/v1/admin/**",
                        "/api/v1/auth/me"
                )
                .excludePathPatterns(
                        "/api/v1/reservations/times",
                        "/api/v1/reservations/times/**"
                );
    }
}
