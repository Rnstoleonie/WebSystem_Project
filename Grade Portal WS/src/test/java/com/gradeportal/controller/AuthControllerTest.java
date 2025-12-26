package com.gradeportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradeportal.dto.auth.LoginRequest;
import com.gradeportal.dto.auth.SignupRequest;
import com.gradeportal.entity.User;
import com.gradeportal.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureWebMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @MockBean
    private com.gradeportal.config.JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_ValidCredentials_ReturnsToken() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(User.UserRole.STUDENT);

        Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userService.authenticateUser("testuser", "password")).thenReturn(user);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    void login_InvalidCredentials_ReturnsBadRequest() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new RuntimeException("Bad credentials"));
        when(userService.authenticateUser("testuser", "wrongpassword")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void signup_ValidRequest_ReturnsSuccess() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest("newuser", "password", "New", "User", User.UserRole.STUDENT);
        User user = new User();
        user.setId(1L);
        user.setUsername("newuser");

        when(userService.signupUser("newuser", "password", "New", "User", User.UserRole.STUDENT))
            .thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully! Please wait for admin approval."));
    }

    @Test
    void signup_ExistingUsername_ReturnsBadRequest() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest("existinguser", "password", "New", "User", User.UserRole.STUDENT);

        when(userService.signupUser("existinguser", "password", "New", "User", User.UserRole.STUDENT))
            .thenThrow(new RuntimeException("Username already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists"));
    }
}
