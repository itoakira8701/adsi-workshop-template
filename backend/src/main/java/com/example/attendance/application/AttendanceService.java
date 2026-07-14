package com.example.attendance.application;

import com.example.attendance.domain.model.Attendance;

import java.util.List;

public interface AttendanceService {

    Attendance clockIn(Long employeeId);

    Attendance clockOut(Long employeeId);

    Attendance getToday(Long employeeId);

    List<Attendance> getMonthly(Long employeeId, String yearMonth);
}
