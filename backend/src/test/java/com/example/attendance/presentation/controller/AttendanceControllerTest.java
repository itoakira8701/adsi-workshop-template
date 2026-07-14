package com.example.attendance.presentation.controller;

import com.example.attendance.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String adminToken() {
        return tokenProvider.generateToken("admin");
    }

    @Test
    @Order(1)
    @DisplayName("GET /attendances/today → 200 (打刻前はnull)")
    void getToday_beforeClockIn_returns200() throws Exception {
        mockMvc.perform(get("/attendances/today")
                .header("Authorization", "Bearer " + adminToken()))
            .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    @DisplayName("POST /attendances/clock-in → 200 出勤打刻成功")
    void clockIn_authenticated_returns200() throws Exception {
        mockMvc.perform(post("/attendances/clock-in")
                .header("Authorization", "Bearer " + adminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.workDate").isNotEmpty())
            .andExpect(jsonPath("$.clockInTime").isNotEmpty())
            .andExpect(jsonPath("$.clockOutTime").isEmpty());
    }

    @Test
    @Order(3)
    @DisplayName("POST /attendances/clock-in → 409 二重打刻")
    void clockIn_duplicate_returns409() throws Exception {
        mockMvc.perform(post("/attendances/clock-in")
                .header("Authorization", "Bearer " + adminToken()))
            .andExpect(status().isConflict());
    }

    @Test
    @Order(4)
    @DisplayName("POST /attendances/clock-out → 200 退勤打刻成功")
    void clockOut_afterClockIn_returns200() throws Exception {
        mockMvc.perform(post("/attendances/clock-out")
                .header("Authorization", "Bearer " + adminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clockOutTime").isNotEmpty())
            .andExpect(jsonPath("$.workingMinutes").isNumber())
            .andExpect(jsonPath("$.overtimeMinutes").isNumber());
    }

    @Test
    @Order(5)
    @DisplayName("GET /attendances/me?yearMonth → 200")
    void getMyAttendances_authenticated_returns200() throws Exception {
        mockMvc.perform(get("/attendances/me")
                .param("yearMonth", "2026-07")
                .header("Authorization", "Bearer " + adminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.yearMonth").value("2026-07"))
            .andExpect(jsonPath("$.attendances").isArray())
            .andExpect(jsonPath("$.attendances[0].clockInTime").isNotEmpty());
    }

    @Test
    @Order(6)
    @DisplayName("認証なしで 401")
    void clockIn_noAuth_returns401() throws Exception {
        mockMvc.perform(post("/attendances/clock-in"))
            .andExpect(status().isUnauthorized());
    }
}
