export interface AttendanceData {
  id: number;
  workDate: string;
  clockInTime: string | null;
  clockOutTime: string | null;
  workingMinutes: number | null;
  overtimeMinutes: number | null;
}

export interface MonthlyData {
  yearMonth: string;
  employeeId: number;
  attendances: AttendanceData[];
  totalWorkingMinutes: number;
  totalOvertimeMinutes: number;
}
