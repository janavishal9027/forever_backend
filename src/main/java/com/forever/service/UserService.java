package com.forever.service;

import com.forever.dto.LoginRequest;
import com.forever.dto.RegisterRequest;
import com.forever.dto.TokenResponse;
import com.forever.entity.User;
import com.forever.repository.UserRepository;
import com.forever.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser.getId());

        return new TokenResponse(true, token);
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User doesn't exist"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getId());
        return new TokenResponse(true, token);
    }

    public TokenResponse adminLogin(LoginRequest request) {
        if (request.getEmail().equals(adminEmail) && request.getPassword().equals(adminPassword)) {
            String token = jwtUtil.generateAdminToken(request.getEmail());
            return new TokenResponse(true, token);
        }
        throw new RuntimeException("Invalid credentials");
    }
}
