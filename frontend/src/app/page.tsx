"use client";

import AuthGuard from "@/components/AuthGuard";
import Navigation from "@/components/Navigation";
import { getStoredEmployee } from "@/lib/auth";

export default function DashboardPage() {
  return (
    <AuthGuard>
      <Navigation />
      <main className="flex-1 p-6">
        <DashboardContent />
      </main>
    </AuthGuard>
  );
}

function DashboardContent() {
  const employee = getStoredEmployee();
  const today = new Date().toLocaleDateString("ja-JP", {
    year: "numeric",
    month: "long",
    day: "numeric",
    weekday: "long",
  });

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-2xl font-bold mb-2">ダッシュボード</h1>
      <p className="text-gray-600 mb-8">{today}</p>

      <div className="bg-white rounded-lg shadow p-6">
        <p className="text-gray-500 mb-4">
          ようこそ、{employee?.name}さん
        </p>
        <p className="text-sm text-gray-400">
          打刻機能は Unit 1 で実装されます。
        </p>
      </div>
    </div>
  );
}
