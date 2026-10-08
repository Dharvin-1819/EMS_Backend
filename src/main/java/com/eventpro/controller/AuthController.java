package com.eventpro.controller;

import com.eventpro.dto.JwtResponse;
import com.eventpro.dto.LoginRequest;
import com.eventpro.dto.SignupRequest;
import com.eventpro.entity.User;
import com.eventpro.repository.UserRepository;
import com.eventpro.security.JwtUtil;
import com.eventpro.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        String displayName = userDetails.getName();
        if (displayName == null || displayName.trim().isEmpty() || displayName.contains("@")) {
            displayName = userRepository.findById(userDetails.getId())
                    .map(User::getName)
                    .orElse(userDetails.getUsername().split("@")[0]);
        }

        return ResponseEntity.ok(new JwtResponse(jwt, 
                                                 userDetails.getId(), 
                                                 displayName, 
                                                 userDetails.getUsername(), 
                                                 roles));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        // Create new user's account
        User user = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .phone(signUpRequest.getPhone())
                .joinedAt(LocalDate.now())
                .status("pending")
                .build();

        String strRole = signUpRequest.getRole();
        if (strRole == null) {
            user.setRole(User.Role.user);
        } else {
            try {
                user.setRole(User.Role.valueOf(strRole.toLowerCase()));
            } catch (IllegalArgumentException e) {
                user.setRole(User.Role.user);
            }
        }

        User saved = userRepository.save(user);

        return ResponseEntity.ok(java.util.Map.of(
            "message", "User registered successfully!",
            "id", saved.getId(),
            "name", saved.getName(),
            "email", saved.getEmail(),
            "role", saved.getRole().name(),
            "status", saved.getStatus()
        ));
    }
}
