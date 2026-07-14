package com.example.attendance.application;

import com.example.attendance.domain.exception.ResourceNotFoundException;
import com.example.attendance.domain.model.ApprovalStatus;
import com.example.attendance.domain.model.Employee;
import com.example.attendance.domain.model.MonthlyApproval;
import com.example.attendance.domain.repository.AttendanceRepository;
import com.example.attendance.domain.repository.EmployeeRepository;
import com.example.attendance.domain.repository.MonthlyApprovalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional
public class ApprovalServiceImpl implements ApprovalService {

    private final MonthlyApprovalRepository approvalRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    public ApprovalServiceImpl(
            MonthlyApprovalRepository approvalRepository,
            EmployeeRepository employeeRepository,
            AttendanceRepository attendanceRepository) {
        this.approvalRepository = approvalRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalSummary> getApprovalList(String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<Employee> employees = employeeRepository.findByActiveTrue();

        return employees.stream().map(emp -> {
            var attendances = attendanceRepository
                .findByEmployeeIdAndWorkDateBetweenOrderByWorkDate(emp.getId(), startDate, endDate);

            int totalWorking = attendances.stream()
                .mapToInt(a -> a.getWorkingMinutes() != null ? a.getWorkingMinutes() : 0)
                .sum();
            int totalOvertime = attendances.stream()
                .mapToInt(a -> a.getOvertimeMinutes() != null ? a.getOvertimeMinutes() : 0)
                .sum();

            String status = approvalRepository.findByEmployeeIdAndYearMonth(emp.getId(), yearMonth)
                .map(a -> a.getStatus().name())
                .orElse(ApprovalStatus.PENDING.name());

            return new ApprovalSummary(emp.getId(), emp.getName(), yearMonth, status, totalWorking, totalOvertime);
        }).toList();
    }

    @Override
    public MonthlyApproval approve(Long employeeId, String yearMonth, Long approverId) {
        employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("社員が見つかりません: id=" + employeeId));

        MonthlyApproval approval = approvalRepository.findByEmployeeIdAndYearMonth(employeeId, yearMonth)
            .orElseGet(() -> MonthlyApproval.builder()
                .employeeId(employeeId)
                .yearMonth(yearMonth)
                .status(ApprovalStatus.PENDING)
                .build());

        if (approval.getStatus() == ApprovalStatus.APPROVED) {
            return approval;
        }

        approval.setStatus(ApprovalStatus.APPROVED);
        approval.setApproverId(approverId);
        approval.setApprovedAt(LocalDateTime.now());

        return approvalRepository.save(approval);
    }
}
