package com.example.attendance.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ApproveRequest(
    @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}", message = "YYYY-MM形式で指定してください") String yearMonth
) {}
