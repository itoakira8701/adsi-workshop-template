"use client";

import { useEffect, useState } from "react";
import AuthGuard from "@/components/AuthGuard";
import Navigation from "@/components/Navigation";
import ClockButton from "@/components/ClockButton";
import { fetchApi } from "@/lib/api-client";
import { getStoredEmployee } from "@/lib/auth";

interface AttendanceData {
  id: number;
  workDate: string;
  clockInTime: string | null;
  clockOutTime: string | null;
  workingMinutes: number | null;
  overtimeMinutes: number | null;
}

export default function DashboardPage() {
  return (
    <AuthGuard>
      <Navigation />
      <main className="flex-1 p-6 bg-gray-50">
        <DashboardContent />
      </main>
    </AuthGuard>
  );
}

function DashboardContent() {
  const employee = getStoredEmployee();
  const [today, setToday] = useState<AttendanceData | null>(null);
  const [loaded, setLoaded] = useState(false);

  const todayStr = new Date().toLocaleDateString("ja-JP", {
    year: "numeric",
    month: "long",
    day: "numeric",
    weekday: "long",
  });

  useEffect(() => {
    let cancelled = false;
    fetchApi<AttendanceData | null>("/attendances/today")
      .then((data) => { if (!cancelled) setToday(data); })
      .catch(() => {})
      .finally(() => { if (!cancelled) setLoaded(true); });
    return () => { cancelled = true; };
  }, []);

  if (!loaded) {
    return (
      <div className="max-w-md mx-auto bg-white rounded-lg shadow p-8 text-center text-gray-500">
        読み込み中...
      </div>
    );
  }

  return (
    <div className="max-w-md mx-auto">
      <h1 className="text-2xl font-bold mb-2">ダッシュボード</h1>
      <p className="text-gray-600 mb-6">{todayStr}</p>

      <div className="bg-white rounded-lg shadow p-6">
        <p className="text-gray-500 mb-6">
          {employee?.name}さん
        </p>
        <ClockButton today={today} onUpdate={setToday} />
      </div>
    </div>
  );
}
