package com.example.attendance.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class WorkDurationTest {

    @Test
    @DisplayName("9:15→17:30 で勤務435分・残業0分")
    void calculate_standardHours_noOvertime() {
        WorkDuration result = WorkDuration.calculate(
            LocalTime.of(9, 15), LocalTime.of(17, 30));

        assertThat(result.workingMinutes()).isEqualTo(435);
        assertThat(result.overtimeMinutes()).isEqualTo(0);
    }

    @Test
    @DisplayName("9:00→18:30 で勤務510分・残業75分")
    void calculate_overtime_returnsPositiveOvertime() {
        WorkDuration result = WorkDuration.calculate(
            LocalTime.of(9, 0), LocalTime.of(18, 30));

        assertThat(result.workingMinutes()).isEqualTo(510);
        assertThat(result.overtimeMinutes()).isEqualTo(75);
    }

    @Test
    @DisplayName("9:15→16:00 で所定未満の場合、残業0分")
    void calculate_underStandard_zeroOvertime() {
        WorkDuration result = WorkDuration.calculate(
            LocalTime.of(9, 15), LocalTime.of(16, 0));

        assertThat(result.workingMinutes()).isEqualTo(345);
        assertThat(result.overtimeMinutes()).isEqualTo(0);
    }
}
