package com.example.MediConnect_Backend.config;

import com.example.MediConnect_Backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of("http://localhost:4200"));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE","PATCH"));
            config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
            return config;
        })).csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/auth/refresh",
                                "/api/doctors/top-rated",
                                "/api/doctors/all"
                        ).permitAll()

                        .requestMatchers(
                                "/api/patients/me/**",   // Patient can manage their own profile
                                "/api/appointments/book",   // Patient can book an appointment
                                "/api/patient-appointments/**",
                                "/api/patient-consultations/**"
                                ).hasRole("PATIENT")
                        // In your SecurityConfig.java
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",     // This is the critical one for config
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(
                                "/api/doctors/**",
                                "/api/doctors/user/change-password",
                                "/api/doctors/availability/**",
                                "/api/appointments/doctor",    // Doctor can get their list of appointments
                                "/api/appointments/{appointmentId}/status", // Doctor can update a specific appointment
                                "/api/consultations/**",
                                "/api/patient-consultations/**",
                                "/api/doctor-panel/**"
                        ).hasRole("DOCTOR")
                        .requestMatchers("/api/consultations/appointment/**").authenticated()
                        .requestMatchers("/api/notifications").authenticated()
                        .requestMatchers("/api/notifications/**").authenticated()
                        .requestMatchers("/api/notifications/unread-count").authenticated()
                        .anyRequest().authenticated()).sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}