package com.example.attendance.presentation.controller;

import com.example.attendance.application.AttendanceService;
import com.example.attendance.domain.model.Attendance;
import com.example.attendance.infrastructure.security.CustomUserDetails;
import com.example.attendance.presentation.dto.AttendanceResponse;
import com.example.attendance.presentation.dto.MonthlyAttendanceResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendances")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/clock-in")
    public AttendanceResponse clockIn(@AuthenticationPrincipal CustomUserDetails user) {
        Attendance attendance = attendanceService.clockIn(user.employee().getId());
        return AttendanceResponse.from(attendance);
    }

    @PostMapping("/clock-out")
    public AttendanceResponse clockOut(@AuthenticationPrincipal CustomUserDetails user) {
        Attendance attendance = attendanceService.clockOut(user.employee().getId());
        return AttendanceResponse.from(attendance);
    }

    @GetMapping("/today")
    public AttendanceResponse getToday(@AuthenticationPrincipal CustomUserDetails user) {
        Attendance attendance = attendanceService.getToday(user.employee().getId());
        return AttendanceResponse.from(attendance);
    }

    @GetMapping("/me")
    public MonthlyAttendanceResponse getMyAttendances(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam String yearMonth) {
        Long employeeId = user.employee().getId();
        List<Attendance> attendances = attendanceService.getMonthly(employeeId, yearMonth);
        return MonthlyAttendanceResponse.from(employeeId, yearMonth, attendances);
    }

    @GetMapping("/employees/{employeeId}")
    @PreAuthorize("hasAnyRole('APPROVER', 'HR')")
    public MonthlyAttendanceResponse getEmployeeAttendances(
            @PathVariable Long employeeId,
            @RequestParam String yearMonth) {
        List<Attendance> attendances = attendanceService.getMonthly(employeeId, yearMonth);
        return MonthlyAttendanceResponse.from(employeeId, yearMonth, attendances);
    }
}
