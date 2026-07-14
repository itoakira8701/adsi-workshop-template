package com.example.attendance.domain.repository;

import com.example.attendance.domain.model.MonthlyApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonthlyApprovalRepository extends JpaRepository<MonthlyApproval, Long> {

    Optional<MonthlyApproval> findByEmployeeIdAndYearMonth(Long employeeId, String yearMonth);

    List<MonthlyApproval> findByYearMonth(String yearMonth);
}
