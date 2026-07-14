package com.example.attendance.domain.model;

import java.time.Duration;
import java.time.LocalTime;

public record WorkDuration(int workingMinutes, int overtimeMinutes) {

    private static final int BREAK_MINUTES = 60;
    private static final int STANDARD_WORKING_MINUTES = 435;

    public static WorkDuration calculate(LocalTime clockIn, LocalTime clockOut) {
        int totalMinutes = (int) Duration.between(clockIn, clockOut).toMinutes();
        int workingMinutes = totalMinutes - BREAK_MINUTES;
        int overtimeMinutes = Math.max(0, workingMinutes - STANDARD_WORKING_MINUTES);
        return new WorkDuration(workingMinutes, overtimeMinutes);
    }
}
