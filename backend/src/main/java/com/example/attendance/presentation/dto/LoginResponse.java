package com.example.attendance.presentation.dto;

public record LoginResponse(
    String token,
    EmployeeResponse employee
) {}
