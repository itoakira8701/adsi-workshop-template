package com.example.attendance.presentation.dto;

import com.example.attendance.domain.model.Attendance;

import java.util.List;

public record MonthlyAttendanceResponse(
    String yearMonth,
    Long employeeId,
    List<AttendanceResponse> attendances,
    int totalWorkingMinutes,
    int totalOvertimeMinutes
) {
    public static MonthlyAttendanceResponse from(Long employeeId, String yearMonth, List<Attendance> attendances) {
        List<AttendanceResponse> responses = attendances.stream()
            .map(AttendanceResponse::from)
            .toList();
        int totalWorking = attendances.stream()
            .mapToInt(a -> a.getWorkingMinutes() != null ? a.getWorkingMinutes() : 0)
            .sum();
        int totalOvertime = attendances.stream()
            .mapToInt(a -> a.getOvertimeMinutes() != null ? a.getOvertimeMinutes() : 0)
            .sum();
        return new MonthlyAttendanceResponse(yearMonth, employeeId, responses, totalWorking, totalOvertime);
    }
}
