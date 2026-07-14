package com.example.attendance.presentation.dto;

import com.example.attendance.domain.model.Attendance;

import java.time.LocalDate;
import java.time.LocalTime;

public record AttendanceResponse(
    Long id,
    LocalDate workDate,
    LocalTime clockInTime,
    LocalTime clockOutTime,
    Integer workingMinutes,
    Integer overtimeMinutes
) {
    public static AttendanceResponse from(Attendance attendance) {
        if (attendance == null) return null;
        return new AttendanceResponse(
            attendance.getId(),
            attendance.getWorkDate(),
            attendance.getClockInTime(),
            attendance.getClockOutTime(),
            attendance.getWorkingMinutes(),
            attendance.getOvertimeMinutes()
        );
    }
}
