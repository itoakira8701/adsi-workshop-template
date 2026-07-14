package com.example.attendance.presentation.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApprovalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String hrToken() {
        return tokenProvider.generateToken("admin");
    }

    @Test
    @DisplayName("APPROVER/HR で GET /approvals → 200")
    void getApprovalList_hrRole_returns200() throws Exception {
        mockMvc.perform(get("/approvals")
                .param("yearMonth", "2026-07")
                .header("Authorization", "Bearer " + hrToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].employeeName").isNotEmpty())
            .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("EMPLOYEE で GET /approvals → 403")
    void getApprovalList_employeeRole_returns403() throws Exception {
        // Create employee user
        String body = """
            {"employeeCode":"approvaltest","name":"テスト社員","email":"approvaltest@example.com","password":"password123","role":"EMPLOYEE"}
            """;
        mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/employees")
                .header("Authorization", "Bearer " + hrToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        String employeeToken = tokenProvider.generateToken("approvaltest");

        mockMvc.perform(get("/approvals")
                .param("yearMonth", "2026-07")
                .header("Authorization", "Bearer " + employeeToken))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("HR で PUT /approvals/{id} → 200 承認成功")
    void approve_hrRole_returns200() throws Exception {
        String body = """
            {"yearMonth": "2026-07"}
            """;

        mockMvc.perform(put("/approvals/1")
                .header("Authorization", "Bearer " + hrToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("APPROVED"))
            .andExpect(jsonPath("$.approverId").isNumber());
    }
}
