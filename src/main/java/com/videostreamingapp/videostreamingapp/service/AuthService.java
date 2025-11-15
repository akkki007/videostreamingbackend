package com.videostreamingapp.videostreamingapp.service;

import com.videostreamingapp.videostreamingapp.dto.AuthResponse;
import com.videostreamingapp.videostreamingapp.model.User;
import com.videostreamingapp.videostreamingapp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(String username, String email, String password) {
        User user = userService.registerUser(username, email, password);
        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        return new AuthResponse(token, user.getUsername(), user.getId());
    }

    public AuthResponse login(String username, String password) {
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = userOpt.get();
        if (!userService.validatePassword(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        return new AuthResponse(token, user.getUsername(), user.getId());
    }
}

