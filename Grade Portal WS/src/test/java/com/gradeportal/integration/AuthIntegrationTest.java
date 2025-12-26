package com.gradeportal.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradeportal.dto.auth.LoginRequest;
import com.gradeportal.dto.auth.SignupRequest;
import com.gradeportal.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullAuthenticationFlow_ShouldWork() throws Exception {
        // 1. Test signup
        SignupRequest signupRequest = new SignupRequest(
            "testuser",
            "password123",
            "Test",
            "User",
            User.UserRole.STUDENT
        );

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully! Please wait for admin approval."));

        // 2. Test login with existing admin user
        LoginRequest loginRequest = new LoginRequest("admin", "password");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.username").value("admin"))
                .andExpect(jsonPath("$.user.role").value("ADMIN"))
                .andReturn();

        // Extract token from response
        String responseContent = loginResult.getResponse().getContentAsString();
        String token = extractTokenFromResponse(responseContent);

        // 3. Test accessing protected endpoint with token
        mockMvc.perform(post("/api/users/pending")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void invalidLogin_ShouldReturnUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistent", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void signupWithExistingUsername_ShouldFail() throws Exception {
        // First signup
        SignupRequest signupRequest1 = new SignupRequest(
            "duplicateuser",
            "password123",
            "Test",
            "User",
            User.UserRole.STUDENT
        );

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest1)))
                .andExpect(status().isOk());

        // Second signup with same username
        SignupRequest signupRequest2 = new SignupRequest(
            "duplicateuser",
            "differentpassword",
            "Different",
            "User",
            User.UserRole.TEACHER
        );

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest2)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists"));
    }

    private String extractTokenFromResponse(String responseContent) throws Exception {
        // Simple JSON parsing to extract token
        int tokenStart = responseContent.indexOf("\"token\":\"") + 9;
        int tokenEnd = responseContent.indexOf("\"", tokenStart);
        return responseContent.substring(tokenStart, tokenEnd);
    }
}
