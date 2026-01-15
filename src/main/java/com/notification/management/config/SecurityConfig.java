package com.notification.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors(Customizer.withDefaults())
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/actuator/**", "/h2-console/**",
                                                                "/api/v1/management/**",
                                                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                                                "/api/v1/notifications/process",
                                                                "/api/v1/onboarding/**",
                                                                "/api/v1/channels/**",
                                                                "/api/v1/templates/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .httpBasic(Customizer.withDefaults())
                                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

                return http.build();
        }

        @Bean
        public UserDetailsService userDetailsService() {
                UserDetails admin = User.withDefaultPasswordEncoder()
                                .username("admin")
                                .password("admin")
                                .roles("ADMIN")
                                .build();
                return new InMemoryUserDetailsManager(admin);
        }
}
