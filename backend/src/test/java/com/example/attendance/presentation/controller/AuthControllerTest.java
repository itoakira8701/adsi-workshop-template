package com.example.attendance.presentation.controller;

import com.example.attendance.domain.model.Employee;
import com.example.attendance.domain.model.Role;
import com.example.attendance.infrastructure.security.CustomUserDetails;
import com.example.attendance.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Test
    @DisplayName("正しい認証情報でトークンが返る")
    void login_validCredentials_returnsToken() throws Exception {
        String body = """
            {"employeeCode": "admin", "password": "admin123"}
            """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.employee.employeeCode").value("admin"))
            .andExpect(jsonPath("$.employee.role").value("HR"));
    }

    @Test
    @DisplayName("不正な認証情報で401が返る")
    void login_invalidCredentials_returns401() throws Exception {
        String body = """
            {"employeeCode": "admin", "password": "wrongpassword"}
            """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("認証なしで /api/auth/me が401")
    void me_noAuth_returns401() throws Exception {
        mockMvc.perform(get("/auth/me"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("認証ありで /api/auth/me が200")
    void me_withAuth_returns200() throws Exception {
        String token = tokenProvider.generateToken("admin");

        mockMvc.perform(get("/auth/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.employeeCode").value("admin"))
            .andExpect(jsonPath("$.name").value("管理者"));
    }
}
