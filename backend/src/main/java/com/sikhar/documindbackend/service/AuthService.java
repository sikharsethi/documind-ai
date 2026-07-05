package com.sikhar.documindbackend.service;

import com.sikhar.documindbackend.dto.AuthResponse;
import com.sikhar.documindbackend.dto.LoginRequest;
import com.sikhar.documindbackend.dto.RegisterRequest;
import com.sikhar.documindbackend.model.User;
import com.sikhar.documindbackend.repository.UserRepository;
import com.sikhar.documindbackend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {

        // 1. Email already exist karta hai?
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }

        // 2. Username already exist karta hai?
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken!");
        }

        // 3. Naya user banao — password hash karke save karo
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        // BCrypt se hash karo — plain text kabhi store mat karo
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 4. Database mein save karo
        userRepository.save(user);

        // 5. JWT token generate karo
        String token = jwtUtil.generateToken(user.getEmail());

        // 6. Token + user info return karo
        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {

        // 1. Spring Security se authenticate karo
        // Yeh automatically email + password verify karta hai
        // Galat credentials pe exception throw hoga
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Database se user load karo
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found!"));

        // 3. JWT token generate karo
        String token = jwtUtil.generateToken(user.getEmail());

        // 4. Token + user info return karo
        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }
}
