package com.example.attendance.application;

import com.example.attendance.domain.model.MonthlyApproval;

import java.util.List;

public interface ApprovalService {

    List<ApprovalSummary> getApprovalList(String yearMonth);

    MonthlyApproval approve(Long employeeId, String yearMonth, Long approverId);

    record ApprovalSummary(
        Long employeeId,
        String employeeName,
        String yearMonth,
        String status,
        int totalWorkingMinutes,
        int totalOvertimeMinutes
    ) {}
}
