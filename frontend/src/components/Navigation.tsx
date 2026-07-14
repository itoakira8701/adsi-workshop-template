"use client";

import Link from "next/link";
import { getStoredEmployee, logout } from "@/lib/auth";

export default function Navigation() {
  const employee = getStoredEmployee();

  if (!employee) return null;

  return (
    <nav className="bg-gray-800 text-white px-6 py-3 flex items-center justify-between">
      <div className="flex items-center gap-6">
        <Link href="/" className="font-bold text-lg">
          勤怠管理
        </Link>
        <Link href="/attendances" className="hover:text-gray-300">
          勤怠一覧
        </Link>
        {(employee.role === "APPROVER" || employee.role === "HR") && (
          <Link href="/approvals" className="hover:text-gray-300">
            承認
          </Link>
        )}
        {employee.role === "HR" && (
          <Link href="/employees" className="hover:text-gray-300">
            社員管理
          </Link>
        )}
      </div>
      <div className="flex items-center gap-4">
        <span className="text-sm">{employee.name}</span>
        <button
          onClick={logout}
          className="text-sm bg-gray-700 px-3 py-1 rounded hover:bg-gray-600"
        >
          ログアウト
        </button>
      </div>
    </nav>
  );
}
