package com.example.attendance.domain.repository;

import com.example.attendance.domain.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);

    List<Attendance> findByEmployeeIdAndWorkDateBetweenOrderByWorkDate(
            Long employeeId, LocalDate startDate, LocalDate endDate);
}
