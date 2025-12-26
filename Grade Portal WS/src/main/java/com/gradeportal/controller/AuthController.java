package com.gradeportal.controller;

import com.gradeportal.dto.auth.LoginRequest;
import com.gradeportal.dto.auth.LoginResponse;
import com.gradeportal.dto.auth.SignupRequest;
import com.gradeportal.entity.User;
import com.gradeportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private com.gradeportal.config.JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
            
            if (user == null) {
                return ResponseEntity.badRequest().body("Invalid credentials");
            }

            // Generate token
            String token = jwtTokenProvider.generateToken(authentication);

            LoginResponse loginResponse = new LoginResponse(token, user);
            
            return ResponseEntity.ok(loginResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            User user = userService.signupUser(
                signUpRequest.getUsername(),
                signUpRequest.getPassword(),
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getRole()
            );

            return ResponseEntity.ok("User registered successfully! Please wait for admin approval.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
