package com.sikhar.documindbackend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
                // CSRF disable — REST API mein zarurat nahi
                // (CSRF sirf browser-based form submissions ke liye hota hai)
                .csrf(csrf -> csrf.disable())

                // Kaunse routes public, kaunse protected
                .authorizeHttpRequests(auth -> auth
                        // Yeh routes bina token ke accessible hain
                        .requestMatchers("/api/auth/**").permitAll()
                        // Baaki sab ke liye token required
                        .anyRequest().authenticated()
                )

                // Session mat banao — JWT stateless hai
                // Har request apna token saath laayegi
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Authentication provider set karo
                .authenticationProvider(authenticationProvider())

                // JwtFilter ko UsernamePasswordAuthenticationFilter se pehle lagao
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt — industry standard password hashing
        // Automatically salt add karta hai, rainbow table attacks se bachata hai
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // Database se user load karne ka service
        provider.setUserDetailsService(userDetailsService);
        // Password verify karne ka encoder
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
