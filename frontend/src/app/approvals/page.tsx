"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import AuthGuard from "@/components/AuthGuard";
import Navigation from "@/components/Navigation";
import { fetchApi, ApiError } from "@/lib/api-client";

interface ApprovalSummary {
  employeeId: number;
  employeeName: string;
  yearMonth: string;
  status: string;
  totalWorkingMinutes: number;
  totalOvertimeMinutes: number;
}

export default function ApprovalsPage() {
  return (
    <AuthGuard>
      <Navigation />
      <main className="flex-1 p-6 bg-gray-50">
        <ApprovalList />
      </main>
    </AuthGuard>
  );
}

function ApprovalList() {
  const now = new Date();
  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [data, setData] = useState<ApprovalSummary[]>([]);
  const [loading, setLoading] = useState(true);

  const yearMonth = `${year}-${String(month).padStart(2, "0")}`;

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      try {
        const d = await fetchApi<ApprovalSummary[]>(`/approvals?yearMonth=${yearMonth}`);
        if (!cancelled) setData(d);
      } catch {
        if (!cancelled) setData([]);
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

  const handleApprove = async (employeeId: number, employeeName: string) => {
    if (!confirm(`${employeeName}の${year}年${month}月の勤怠を承認しますか？`)) return;
    try {
      await fetchApi(`/approvals/${employeeId}`, {
        method: "PUT",
        body: JSON.stringify({ yearMonth }),
      });
      setData(data.map(d =>
        d.employeeId === employeeId ? { ...d, status: "APPROVED" } : d
      ));
    } catch (err) {
      alert(err instanceof ApiError ? err.message : "承認に失敗しました");
    }
  };

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">月次承認</h1>

      <div className="flex items-center gap-4 mb-6">
        <button onClick={prevMonth} className="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300">&lt;</button>
        <span className="text-lg font-medium">{year}年{month}月</span>
        <button onClick={nextMonth} className="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300">&gt;</button>
      </div>

      {loading && (
        <div className="bg-white shadow rounded-lg p-8 text-center text-gray-500">読み込み中...</div>
      )}

      {!loading && (
        <div className="bg-white shadow rounded-lg overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b">
              <tr>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">社員名</th>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">勤務時間</th>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">残業時間</th>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">状態</th>
                <th className="px-4 py-3 text-right text-sm font-medium text-gray-700">操作</th>
              </tr>
            </thead>
            <tbody>
              {data.map((item) => (
                <tr key={item.employeeId} className="border-t hover:bg-gray-50">
                  <td className="px-4 py-3 text-sm">{item.employeeName}</td>
                  <td className="px-4 py-3 text-sm">{formatMinutes(item.totalWorkingMinutes)}</td>
                  <td className="px-4 py-3 text-sm">{formatMinutes(item.totalOvertimeMinutes)}</td>
                  <td className="px-4 py-3 text-sm">
                    <span className={`inline-block px-2 py-0.5 rounded text-xs font-medium ${
                      item.status === "APPROVED"
                        ? "bg-green-100 text-green-700"
                        : "bg-yellow-100 text-yellow-700"
                    }`}>
                      {item.status === "APPROVED" ? "承認済" : "未承認"}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-sm text-right space-x-3">
                    <Link
                      href={`/approvals/${item.employeeId}?yearMonth=${yearMonth}`}
                      className="text-blue-600 hover:underline"
                    >
                      詳細
                    </Link>
                    {item.status !== "APPROVED" && (
                      <button
                        onClick={() => handleApprove(item.employeeId, item.employeeName)}
                        className="text-green-600 hover:underline font-medium"
                      >
                        承認
                      </button>
                    )}
                  </td>
                </tr>
              ))}
              {data.length === 0 && (
                <tr>
                  <td colSpan={5} className="px-4 py-8 text-center text-gray-500">対象社員がいません</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

function formatMinutes(minutes: number): string {
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return `${h}:${String(m).padStart(2, "0")}`;
}
