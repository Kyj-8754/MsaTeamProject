package com.msa.kyj_prj;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http)) // CORS 활성화
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()      // 모든 요청 허용 (테스트용)
            );

        return http.build();
    }
}