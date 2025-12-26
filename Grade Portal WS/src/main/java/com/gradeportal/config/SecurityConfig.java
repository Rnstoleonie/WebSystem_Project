package com.gradeportal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()

                // Public static resources
                .requestMatchers("/", "/index.html", "/signup.html", "/admin.html", "/teacher.html", "/student.html", "/dashboard.html", "/css/**", "/js/**", "/assets/**").permitAll()
                
                // Admin endpoints
                .requestMatchers(HttpMethod.GET, "/api/users/teachers").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users/pending").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}/approve").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}/decline").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}/assign").hasRole("ADMIN")
                
                // Teacher endpoints
                .requestMatchers(HttpMethod.GET, "/api/students").hasRole("TEACHER")
                .requestMatchers(HttpMethod.POST, "/api/students").hasRole("TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/students/{id}").hasRole("TEACHER")
                .requestMatchers(HttpMethod.DELETE, "/api/students/{id}").hasRole("TEACHER")
                .requestMatchers(HttpMethod.GET, "/api/subjects").hasRole("TEACHER")
                .requestMatchers(HttpMethod.GET, "/api/grades/student/{id}").hasRole("TEACHER")
                .requestMatchers(HttpMethod.POST, "/api/grades").hasRole("TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/grades/{id}").hasRole("TEACHER")
                .requestMatchers(HttpMethod.DELETE, "/api/grades/{id}").hasRole("TEACHER")
                
                // Student endpoints
                .requestMatchers(HttpMethod.GET, "/api/grades/student/{id}/report").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users/current").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/students/user/{userId}").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
