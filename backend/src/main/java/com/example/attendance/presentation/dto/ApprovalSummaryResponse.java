package com.example.attendance.presentation.dto;

import com.example.attendance.application.ApprovalService.ApprovalSummary;

public record ApprovalSummaryResponse(
    Long employeeId,
    String employeeName,
    String yearMonth,
    String status,
    int totalWorkingMinutes,
    int totalOvertimeMinutes
) {
    public static ApprovalSummaryResponse from(ApprovalSummary summary) {
        return new ApprovalSummaryResponse(
            summary.employeeId(),
            summary.employeeName(),
            summary.yearMonth(),
            summary.status(),
            summary.totalWorkingMinutes(),
            summary.totalOvertimeMinutes()
        );
    }
}
