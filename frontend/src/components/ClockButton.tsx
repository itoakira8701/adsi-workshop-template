"use client";

import { useState } from "react";
import { fetchApi, ApiError } from "@/lib/api-client";

interface AttendanceData {
  id: number;
  workDate: string;
  clockInTime: string | null;
  clockOutTime: string | null;
  workingMinutes: number | null;
  overtimeMinutes: number | null;
}

interface ClockButtonProps {
  today: AttendanceData | null;
  onUpdate: (data: AttendanceData) => void;
}

export default function ClockButton({ today, onUpdate }: ClockButtonProps) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const hasClockedIn = today?.clockInTime != null;
  const hasClockedOut = today?.clockOutTime != null;

  const handleClockIn = async () => {
    setError("");
    setLoading(true);
    try {
      const result = await fetchApi<AttendanceData>("/attendances/clock-in", { method: "POST" });
      onUpdate(result);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "打刻に失敗しました");
    } finally {
      setLoading(false);
    }
  };

  const handleClockOut = async () => {
    setError("");
    setLoading(true);
    try {
      const result = await fetchApi<AttendanceData>("/attendances/clock-out", { method: "POST" });
      onUpdate(result);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "打刻に失敗しました");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      {error && (
        <div className="bg-red-50 text-red-600 p-3 rounded mb-4 text-sm">{error}</div>
      )}

      <div className="flex gap-4">
        <button
          onClick={handleClockIn}
          disabled={hasClockedIn || loading}
          className="flex-1 bg-blue-600 text-white py-4 rounded-lg text-lg font-bold hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          出勤
        </button>
        <button
          onClick={handleClockOut}
          disabled={!hasClockedIn || hasClockedOut || loading}
          className="flex-1 bg-orange-600 text-white py-4 rounded-lg text-lg font-bold hover:bg-orange-700 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          退勤
        </button>
      </div>

      <div className="mt-4 text-sm text-gray-600 space-y-1">
        <p>出勤: {today?.clockInTime || "--:--"}</p>
        <p>退勤: {today?.clockOutTime || "--:--"}</p>
        {today?.workingMinutes != null && (
          <p>勤務時間: {formatMinutes(today.workingMinutes)}（残業: {formatMinutes(today.overtimeMinutes || 0)}）</p>
        )}
      </div>
    </div>
  );
}

function formatMinutes(minutes: number): string {
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return `${h}:${String(m).padStart(2, "0")}`;
}
