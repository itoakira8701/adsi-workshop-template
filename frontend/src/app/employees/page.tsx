"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import AuthGuard from "@/components/AuthGuard";
import Navigation from "@/components/Navigation";
import { fetchApi } from "@/lib/api-client";
import { Employee } from "@/lib/auth";

export default function EmployeesPage() {
  return (
    <AuthGuard>
      <Navigation />
      <main className="flex-1 p-6 bg-gray-50">
        <EmployeeList />
      </main>
    </AuthGuard>
  );
}

function EmployeeList() {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;
    fetchApi<Employee[]>("/employees")
      .then((data) => { if (!cancelled) setEmployees(data); })
      .catch((err) => { if (!cancelled) setError(err instanceof Error ? err.message : "取得に失敗しました"); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, []);

  const handleDelete = async (id: number, name: string) => {
    if (!confirm(`${name}を削除しますか？`)) return;
    try {
      await fetchApi(`/employees/${id}`, { method: "DELETE" });
      setEmployees(employees.filter((e) => e.id !== id));
    } catch (err) {
      alert(err instanceof Error ? err.message : "削除に失敗しました");
    }
  };

  const roleLabel = (role: string) => {
    switch (role) {
      case "HR": return "人事";
      case "APPROVER": return "承認者";
      default: return "社員";
    }
  };

  return (
    <div className="max-w-4xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">社員マスタ</h1>
        <Link
          href="/employees/new"
          className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
        >
          + 新規登録
        </Link>
      </div>

      {loading && (
        <div className="bg-white shadow rounded-lg p-8 text-center text-gray-500">
          読み込み中...
        </div>
      )}

      {error && (
        <div className="bg-red-50 text-red-600 p-4 rounded-lg">{error}</div>
      )}

      {!loading && !error && (
        <div className="bg-white shadow rounded-lg overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b">
              <tr>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">社員コード</th>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">氏名</th>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">メール</th>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">ロール</th>
                <th className="px-4 py-3 text-right text-sm font-medium text-gray-700">操作</th>
              </tr>
            </thead>
            <tbody>
              {employees.map((emp) => (
                <tr key={emp.id} className="border-t hover:bg-gray-50">
                  <td className="px-4 py-3 text-sm font-mono">{emp.employeeCode}</td>
                  <td className="px-4 py-3 text-sm">{emp.name}</td>
                  <td className="px-4 py-3 text-sm text-gray-600">{emp.email}</td>
                  <td className="px-4 py-3 text-sm">
                    <span className={`inline-block px-2 py-0.5 rounded text-xs font-medium ${
                      emp.role === "HR" ? "bg-purple-100 text-purple-700" :
                      emp.role === "APPROVER" ? "bg-green-100 text-green-700" :
                      "bg-gray-100 text-gray-700"
                    }`}>
                      {roleLabel(emp.role)}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-sm text-right space-x-3">
                    <Link
                      href={`/employees/${emp.id}/edit`}
                      className="text-blue-600 hover:underline"
                    >
                      編集
                    </Link>
                    <button
                      onClick={() => handleDelete(emp.id, emp.name)}
                      className="text-red-600 hover:underline"
                    >
                      削除
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {employees.length === 0 && (
            <p className="p-6 text-center text-gray-500">社員が登録されていません</p>
          )}
        </div>
      )}
    </div>
  );
}
