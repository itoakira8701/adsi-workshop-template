package com.example.attendance.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank String employeeCode,
    @NotBlank String password
) {}
