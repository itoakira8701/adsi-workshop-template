"use client";

import { useEffect, useState } from "react";
import { useParams, useSearchParams } from "next/navigation";
import Link from "next/link";
import AuthGuard from "@/components/AuthGuard";
import Navigation from "@/components/Navigation";
import { fetchApi } from "@/lib/api-client";
import { MonthlyData } from "@/lib/types";

export default function ApprovalDetailPage() {
  return (
    <AuthGuard>
      <Navigation />
      <main className="flex-1 p-6 bg-gray-50">
        <ApprovalDetail />
      </main>
    </AuthGuard>
  );
}

function ApprovalDetail() {
  const params = useParams();
  const searchParams = useSearchParams();
  const employeeId = params.id;
  const yearMonth = searchParams.get("yearMonth") || getCurrentYearMonth();

  const [data, setData] = useState<MonthlyData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      try {
        const d = await fetchApi<MonthlyData>(`/attendances/employees/${employeeId}?yearMonth=${yearMonth}`);
        if (!cancelled) setData(d);
      } catch {
        if (!cancelled) setData(null);
      } finally {
        if (!cancelled) setLoading(false);
      }
    };
    load();
    return () => { cancelled = true; };
  }, [employeeId, yearMonth]);

  if (loading) {
    return <div className="bg-white shadow rounded-lg p-8 text-center text-gray-500">読み込み中...</div>;
  }

  if (!data) {
    return <div className="text-red-600">データの取得に失敗しました</div>;
  }

  return (
    <div className="max-w-4xl mx-auto">
      <div className="flex items-center gap-4 mb-6">
        <Link href="/approvals" className="text-blue-600 hover:underline">&larr; 承認一覧に戻る</Link>
      </div>

      <h1 className="text-2xl font-bold mb-2">勤怠詳細</h1>
      <p className="text-gray-600 mb-6">{yearMonth.replace("-", "年")}月</p>

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
    </div>
  );
}

function getCurrentYearMonth(): string {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
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
