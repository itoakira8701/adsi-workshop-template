package com.example.attendance.application;

import com.example.attendance.domain.model.ApprovalStatus;
import com.example.attendance.domain.model.Employee;
import com.example.attendance.domain.model.MonthlyApproval;
import com.example.attendance.domain.model.Role;
import com.example.attendance.domain.repository.AttendanceRepository;
import com.example.attendance.domain.repository.EmployeeRepository;
import com.example.attendance.domain.repository.MonthlyApprovalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ApprovalServiceTest {

    @Mock
    private MonthlyApprovalRepository approvalRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private AttendanceRepository attendanceRepository;

    private ApprovalService approvalService;

    @BeforeEach
    void setUp() {
        approvalService = new ApprovalServiceImpl(approvalRepository, employeeRepository, attendanceRepository);
    }

    @Test
    @DisplayName("getApprovalList: 全社員のサマリーが返る")
    void getApprovalList_returnsAllEmployeeSummaries() {
        var emp1 = Employee.builder().id(1L).name("田中").role(Role.EMPLOYEE).active(true).build();
        var emp2 = Employee.builder().id(2L).name("佐藤").role(Role.EMPLOYEE).active(true).build();
        when(employeeRepository.findByActiveTrue()).thenReturn(List.of(emp1, emp2));
        when(attendanceRepository.findByEmployeeIdAndWorkDateBetweenOrderByWorkDate(
            any(), any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of());
        when(approvalRepository.findByEmployeeIdAndYearMonth(any(), any())).thenReturn(Optional.empty());

        List<ApprovalService.ApprovalSummary> result = approvalService.getApprovalList("2026-07");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).employeeName()).isEqualTo("田中");
        assertThat(result.get(0).status()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("approve: PENDING → APPROVED に更新される")
    void approve_pending_updatesToApproved() {
        when(employeeRepository.findById(1L))
            .thenReturn(Optional.of(Employee.builder().id(1L).name("田中").build()));
        when(approvalRepository.findByEmployeeIdAndYearMonth(1L, "2026-07"))
            .thenReturn(Optional.empty());
        when(approvalRepository.save(any(MonthlyApproval.class)))
            .thenAnswer(i -> {
                MonthlyApproval a = i.getArgument(0);
                a.setId(1L);
                return a;
            });

        MonthlyApproval result = approvalService.approve(1L, "2026-07", 99L);

        assertThat(result.getStatus()).isEqualTo(ApprovalStatus.APPROVED);
        assertThat(result.getApproverId()).isEqualTo(99L);
        assertThat(result.getApprovedAt()).isNotNull();
    }

    @Test
    @DisplayName("approve: 既に承認済みの場合は変更しない（冪等）")
    void approve_alreadyApproved_returnsWithoutChange() {
        when(employeeRepository.findById(1L))
            .thenReturn(Optional.of(Employee.builder().id(1L).name("田中").build()));
        var existing = MonthlyApproval.builder()
            .id(1L).employeeId(1L).yearMonth("2026-07")
            .status(ApprovalStatus.APPROVED).approverId(50L).build();
        when(approvalRepository.findByEmployeeIdAndYearMonth(1L, "2026-07"))
            .thenReturn(Optional.of(existing));

        MonthlyApproval result = approvalService.approve(1L, "2026-07", 99L);

        assertThat(result.getApproverId()).isEqualTo(50L);
    }
}
