"use client";

import { useEffect, useState } from "react";
import AuthGuard from "@/components/AuthGuard";
import Navigation from "@/components/Navigation";
import { fetchApi } from "@/lib/api-client";
import { MonthlyData } from "@/lib/types";

export default function AttendancesPage() {
  return (
    <AuthGuard>
      <Navigation />
      <main className="flex-1 p-6 bg-gray-50">
        <AttendanceList />
      </main>
    </AuthGuard>
  );
}

function AttendanceList() {
  const now = new Date();
  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [data, setData] = useState<MonthlyData | null>(null);
  const [loading, setLoading] = useState(true);

  const yearMonth = `${year}-${String(month).padStart(2, "0")}`;

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      try {
        const d = await fetchApi<MonthlyData>(`/attendances/me?yearMonth=${yearMonth}`);
        if (!cancelled) setData(d);
      } catch {
        if (!cancelled) setData(null);
      } finally {
        if (!cancelled) setLoading(false);
      }
    };
    load();
    return () => { cancelled = true; };
  }, [yearMonth]);

  const prevMonth = () => {
    setLoading(true);
    if (month === 1) { setYear(year - 1); setMonth(12); }
    else { setMonth(month - 1); }
  };

  const nextMonth = () => {
    setLoading(true);
    if (month === 12) { setYear(year + 1); setMonth(1); }
    else { setMonth(month + 1); }
  };

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">勤怠一覧</h1>

      <div className="flex items-center gap-4 mb-6">
        <button onClick={prevMonth} className="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300">
          &lt;
        </button>
        <span className="text-lg font-medium">{year}年{month}月</span>
        <button onClick={nextMonth} className="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300">
          &gt;
        </button>
      </div>

      {loading && (
        <div className="bg-white shadow rounded-lg p-8 text-center text-gray-500">読み込み中...</div>
      )}

      {!loading && data && (
        <div className="bg-white shadow rounded-lg overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b">
              <tr>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">日付</th>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">出勤</th>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">退勤</th>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">勤務時間</th>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">残業時間</th>
              </tr>
            </thead>
            <tbody>
              {data.attendances.map((att) => (
                <tr key={att.id} className="border-t hover:bg-gray-50">
                  <td className="px-4 py-3 text-sm">{formatDate(att.workDate)}</td>
                  <td className="px-4 py-3 text-sm">{att.clockInTime || "--:--"}</td>
                  <td className="px-4 py-3 text-sm">{att.clockOutTime || "--:--"}</td>
                  <td className="px-4 py-3 text-sm">{att.workingMinutes != null ? formatMinutes(att.workingMinutes) : "-"}</td>
                  <td className="px-4 py-3 text-sm">{att.overtimeMinutes != null ? formatMinutes(att.overtimeMinutes) : "-"}</td>
                </tr>
              ))}
              {data.attendances.length === 0 && (
                <tr>
                  <td colSpan={5} className="px-4 py-8 text-center text-gray-500">この月の勤怠データはありません</td>
                </tr>
              )}
            </tbody>
            {data.attendances.length > 0 && (
              <tfoot className="bg-gray-50 border-t font-medium">
                <tr>
                  <td className="px-4 py-3 text-sm" colSpan={3}>合計</td>
                  <td className="px-4 py-3 text-sm">{formatMinutes(data.totalWorkingMinutes)}</td>
                  <td className="px-4 py-3 text-sm">{formatMinutes(data.totalOvertimeMinutes)}</td>
                </tr>
              </tfoot>
            )}
          </table>
        </div>
      )}
    </div>
  );
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  const day = date.getDate();
  const weekdays = ["日", "月", "火", "水", "木", "金", "土"];
  const weekday = weekdays[date.getDay()];
  return `${day}日 (${weekday})`;
}

function formatMinutes(minutes: number): string {
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return `${h}:${String(m).padStart(2, "0")}`;
}
