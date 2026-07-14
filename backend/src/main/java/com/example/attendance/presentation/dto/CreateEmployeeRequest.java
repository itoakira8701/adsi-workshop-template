package com.example.attendance.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateEmployeeRequest(
    @NotBlank @Size(max = 50) String employeeCode,
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) String password,
    @NotBlank @Pattern(regexp = "EMPLOYEE|APPROVER|HR", message = "ロールはEMPLOYEE, APPROVER, HRのいずれかを指定してください") String role
) {}
