package com.example.wallet.service;

import com.example.wallet.dto.AuthRequest;
import com.example.wallet.dto.AuthResponse;
import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void register(AuthRequest request){
        if(userRepository.findByEmail(request.email()).isPresent()){
            throw new RuntimeException("Email already registered.");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);
    }

    public AuthResponse login(AuthRequest request){
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials."));
        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new RuntimeException("Invalid credentials.");
        }

        return new AuthResponse(jwtService.generateToken(user.getEmail()));
    }

}
