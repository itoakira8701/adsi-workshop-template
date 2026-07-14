package com.example.attendance.presentation.dto;

import com.example.attendance.domain.model.MonthlyApproval;

import java.time.LocalDateTime;

public record ApprovalResponse(
    Long employeeId,
    String yearMonth,
    String status,
    Long approverId,
    LocalDateTime approvedAt
) {
    public static ApprovalResponse from(MonthlyApproval approval) {
        return new ApprovalResponse(
            approval.getEmployeeId(),
            approval.getYearMonth(),
            approval.getStatus().name(),
            approval.getApproverId(),
            approval.getApprovedAt()
        );
    }
}
