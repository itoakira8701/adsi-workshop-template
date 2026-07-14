package com.example.attendance.presentation.controller;

import com.example.attendance.application.ApprovalService;
import com.example.attendance.infrastructure.security.CustomUserDetails;
import com.example.attendance.presentation.dto.ApprovalResponse;
import com.example.attendance.presentation.dto.ApprovalSummaryResponse;
import com.example.attendance.presentation.dto.ApproveRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/approvals")
@PreAuthorize("hasAnyRole('APPROVER', 'HR')")
@Validated
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping
    public List<ApprovalSummaryResponse> getApprovalList(
            @RequestParam @Pattern(regexp = "\\d{4}-\\d{2}", message = "YYYY-MM形式で指定してください") String yearMonth) {
        return approvalService.getApprovalList(yearMonth).stream()
            .map(ApprovalSummaryResponse::from)
            .toList();
    }

    @PutMapping("/{employeeId}")
    public ApprovalResponse approve(
            @PathVariable Long employeeId,
            @Valid @RequestBody ApproveRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        var approval = approvalService.approve(employeeId, request.yearMonth(), user.employee().getId());
        return ApprovalResponse.from(approval);
    }
}
