package com.devacademy.DevAcademy_BE.config;

import com.devacademy.DevAcademy_BE.auth.CustomLogoutSuccessHandler;
import com.devacademy.DevAcademy_BE.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/ws/**", "/ws/info/**")
                        .permitAll()
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/api/v1/courses/user",
                                "/api/v1/courses/search",
                                "/api/v1/courses/{id}",
                                "api/v1/courses/{id}/chapters",
                                "/api/v1/chapters/{idChapter}/lessons",
                                "/api/v1/categories")
                        .permitAll()
                        .requestMatchers("/api/v1/users/**").hasAuthority(RoleType.ADMIN.name())
                        .requestMatchers("/api/v1/courses/**").hasAnyAuthority(RoleType.TEACHER.name())
                        .requestMatchers("/api/v1/categories/**",
                                "/api/v1/tech-stacks/**",
                                "/api/v1/prompts/**")
                        .hasAnyAuthority(RoleType.TEACHER.name())
                        .anyRequest().authenticated())
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout((logout) -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(new CustomLogoutSuccessHandler()))
                .build();
    }
}
