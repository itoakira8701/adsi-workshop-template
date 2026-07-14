package com.example.attendance.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateEmployeeRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Email String email,
    @NotBlank @Pattern(regexp = "EMPLOYEE|APPROVER|HR", message = "ロールはEMPLOYEE, APPROVER, HRのいずれかを指定してください") String role,
    @NotNull Long version
) {}
