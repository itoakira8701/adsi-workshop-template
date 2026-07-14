package com.example.attendance.application;

import com.example.attendance.domain.model.Attendance;
import com.example.attendance.domain.model.WorkDuration;
import com.example.attendance.domain.repository.AttendanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public Attendance clockIn(Long employeeId) {
        LocalDate today = LocalDate.now();

        attendanceRepository.findByEmployeeIdAndWorkDate(employeeId, today)
            .ifPresent(a -> {
                throw new IllegalStateException("本日は既に出勤打刻済みです");
            });

        Attendance attendance = Attendance.builder()
            .employeeId(employeeId)
            .workDate(today)
            .clockInTime(LocalTime.now())
            .build();

        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance clockOut(Long employeeId) {
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository.findByEmployeeIdAndWorkDate(employeeId, today)
            .orElseThrow(() -> new IllegalStateException("出勤記録がありません。先に出勤打刻してください"));

        if (attendance.getClockOutTime() != null) {
            throw new IllegalStateException("本日は既に退勤打刻済みです");
        }

        LocalTime clockOut = LocalTime.now();
        attendance.setClockOutTime(clockOut);

        WorkDuration duration = WorkDuration.calculate(attendance.getClockInTime(), clockOut);
        attendance.setWorkingMinutes(duration.workingMinutes());
        attendance.setOvertimeMinutes(duration.overtimeMinutes());

        return attendanceRepository.save(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public Attendance getToday(Long employeeId) {
        return attendanceRepository.findByEmployeeIdAndWorkDate(employeeId, LocalDate.now())
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getMonthly(Long employeeId, String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();
        return attendanceRepository.findByEmployeeIdAndWorkDateBetweenOrderByWorkDate(
            employeeId, startDate, endDate);
    }
}
