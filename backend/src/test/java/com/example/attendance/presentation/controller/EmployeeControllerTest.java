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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String hrToken() {
        return tokenProvider.generateToken("admin");
    }

    @Test
    @DisplayName("HRロールでGET /employees → 200 + 一覧")
    void getAll_hrRole_returns200() throws Exception {
        mockMvc.perform(get("/employees")
                .header("Authorization", "Bearer " + hrToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("EMPLOYEEロールでGET /employees → 403")
    void getAll_employeeRole_returns403() throws Exception {
        // Create employee user first
        String body = """
            {"employeeCode":"user01","name":"一般社員","email":"user01@example.com","password":"password123","role":"EMPLOYEE"}
            """;
        mockMvc.perform(post("/employees")
                .header("Authorization", "Bearer " + hrToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());

        String employeeToken = tokenProvider.generateToken("user01");

        mockMvc.perform(get("/employees")
                .header("Authorization", "Bearer " + employeeToken))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("HRロールでPOST /employees → 201")
    void create_hrRole_returns201() throws Exception {
        String body = """
            {"employeeCode":"emp100","name":"新規社員","email":"emp100@example.com","password":"password123","role":"EMPLOYEE"}
            """;

        mockMvc.perform(post("/employees")
                .header("Authorization", "Bearer " + hrToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.employeeCode").value("emp100"))
            .andExpect(jsonPath("$.name").value("新規社員"))
            .andExpect(jsonPath("$.role").value("EMPLOYEE"));
    }

    @Test
    @DisplayName("バリデーションエラーで400")
    void create_invalidRequest_returns400() throws Exception {
        String body = """
            {"employeeCode":"","name":"","email":"invalid","password":"short","role":"EMPLOYEE"}
            """;

        mockMvc.perform(post("/employees")
                .header("Authorization", "Bearer " + hrToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("社員コード重複で409")
    void create_duplicateCode_returns409() throws Exception {
        String body = """
            {"employeeCode":"admin","name":"重複","email":"dup@example.com","password":"password123","role":"EMPLOYEE"}
            """;

        mockMvc.perform(post("/employees")
                .header("Authorization", "Bearer " + hrToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /employees/{id} で更新成功")
    void update_validRequest_returns200() throws Exception {
        String body = """
            {"name":"管理者更新","email":"admin@example.com","role":"HR","version":0}
            """;

        mockMvc.perform(put("/employees/1")
                .header("Authorization", "Bearer " + hrToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("管理者更新"));
    }

    @Test
    @DisplayName("DELETE /employees/{id} で論理削除")
    void delete_existing_returns204() throws Exception {
        String createBody = """
            {"employeeCode":"todelete","name":"削除対象","email":"del@example.com","password":"password123","role":"EMPLOYEE"}
            """;
        var createResult = mockMvc.perform(post("/employees")
                .header("Authorization", "Bearer " + hrToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        int id = org.springframework.test.web.servlet.result.JsonPathResultMatchers.class != null
            ? Integer.parseInt(responseBody.replaceAll(".*\"id\":(\\d+).*", "$1"))
            : 0;

        mockMvc.perform(delete("/employees/" + id)
                .header("Authorization", "Bearer " + hrToken()))
            .andExpect(status().isNoContent());
    }
}
