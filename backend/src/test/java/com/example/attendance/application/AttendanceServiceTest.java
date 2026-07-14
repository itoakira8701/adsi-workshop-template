package com.example.attendance.application;

import com.example.attendance.domain.model.Attendance;
import com.example.attendance.domain.repository.AttendanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    private AttendanceService attendanceService;

    @BeforeEach
    void setUp() {
        attendanceService = new AttendanceServiceImpl(attendanceRepository);
    }

    @Test
    @DisplayName("clockIn: 出勤時刻が記録される")
    void clockIn_noExistingRecord_createsAttendance() {
        when(attendanceRepository.findByEmployeeIdAndWorkDate(1L, LocalDate.now()))
            .thenReturn(Optional.empty());
        when(attendanceRepository.save(any(Attendance.class)))
            .thenAnswer(i -> {
                Attendance a = i.getArgument(0);
                a.setId(1L);
                return a;
            });

        Attendance result = attendanceService.clockIn(1L);

        assertThat(result.getEmployeeId()).isEqualTo(1L);
        assertThat(result.getWorkDate()).isEqualTo(LocalDate.now());
        assertThat(result.getClockInTime()).isNotNull();
        assertThat(result.getClockOutTime()).isNull();
    }

    @Test
    @DisplayName("clockIn: 同日二重打刻時にIllegalStateException")
    void clockIn_alreadyClockedIn_throwsException() {
        Attendance existing = Attendance.builder()
            .id(1L).employeeId(1L).workDate(LocalDate.now())
            .clockInTime(LocalTime.of(9, 15)).build();
        when(attendanceRepository.findByEmployeeIdAndWorkDate(1L, LocalDate.now()))
            .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> attendanceService.clockIn(1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("出勤");
    }

    @Test
    @DisplayName("clockOut: 勤務時間が計算される")
    void clockOut_existingClockIn_calculatesWorkDuration() {
        Attendance existing = Attendance.builder()
            .id(1L).employeeId(1L).workDate(LocalDate.now())
            .clockInTime(LocalTime.of(9, 15)).build();
        when(attendanceRepository.findByEmployeeIdAndWorkDate(1L, LocalDate.now()))
            .thenReturn(Optional.of(existing));
        when(attendanceRepository.save(any(Attendance.class)))
            .thenAnswer(i -> i.getArgument(0));

        Attendance result = attendanceService.clockOut(1L);

        assertThat(result.getClockOutTime()).isNotNull();
        assertThat(result.getWorkingMinutes()).isNotNull();
        assertThat(result.getOvertimeMinutes()).isNotNull();
    }

    @Test
    @DisplayName("clockOut: 出勤記録なし時にIllegalStateException")
    void clockOut_noClockIn_throwsException() {
        when(attendanceRepository.findByEmployeeIdAndWorkDate(1L, LocalDate.now()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.clockOut(1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("出勤");
    }

    @Test
    @DisplayName("clockOut: 既に退勤済みの場合にIllegalStateException")
    void clockOut_alreadyClockedOut_throwsException() {
        Attendance existing = Attendance.builder()
            .id(1L).employeeId(1L).workDate(LocalDate.now())
            .clockInTime(LocalTime.of(9, 15))
            .clockOutTime(LocalTime.of(17, 30)).build();
        when(attendanceRepository.findByEmployeeIdAndWorkDate(1L, LocalDate.now()))
            .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> attendanceService.clockOut(1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("退勤");
    }

    @Test
    @DisplayName("getMonthly: 月次一覧が返る")
    void getMonthly_existingRecords_returnsList() {
        List<Attendance> records = List.of(
            Attendance.builder().id(1L).employeeId(1L).workDate(LocalDate.of(2026, 7, 1))
                .clockInTime(LocalTime.of(9, 15)).clockOutTime(LocalTime.of(17, 30))
                .workingMinutes(435).overtimeMinutes(0).build(),
            Attendance.builder().id(2L).employeeId(1L).workDate(LocalDate.of(2026, 7, 2))
                .clockInTime(LocalTime.of(9, 0)).clockOutTime(LocalTime.of(18, 30))
                .workingMinutes(510).overtimeMinutes(75).build()
        );
        when(attendanceRepository.findByEmployeeIdAndWorkDateBetweenOrderByWorkDate(
            1L, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31)))
            .thenReturn(records);

        List<Attendance> result = attendanceService.getMonthly(1L, "2026-07");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getWorkDate()).isEqualTo(LocalDate.of(2026, 7, 1));
    }
}
