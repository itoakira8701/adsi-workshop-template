"use client";

import { useState } from "react";

interface EmployeeFormProps {
  initialData?: {
    name: string;
    email: string;
    role: string;
  };
  showPassword?: boolean;
  showEmployeeCode?: boolean;
  onSubmit: (data: EmployeeFormData) => Promise<void>;
  submitLabel: string;
}

export interface EmployeeFormData {
  employeeCode?: string;
  name: string;
  email: string;
  password?: string;
  role: string;
}

export default function EmployeeForm({
  initialData,
  showPassword = false,
  showEmployeeCode = false,
  onSubmit,
  submitLabel,
}: EmployeeFormProps) {
  const [employeeCode, setEmployeeCode] = useState("");
  const [name, setName] = useState(initialData?.name || "");
  const [email, setEmail] = useState(initialData?.email || "");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState(initialData?.role || "EMPLOYEE");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      await onSubmit({
        ...(showEmployeeCode && { employeeCode }),
        name,
        email,
        ...(showPassword && { password }),
        role,
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : "エラーが発生しました");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4 max-w-lg">
      {error && (
        <div className="bg-red-50 text-red-600 p-3 rounded text-sm">{error}</div>
      )}

      {showEmployeeCode && (
        <div>
          <label htmlFor="employeeCode" className="block text-sm font-medium text-gray-700 mb-1">
            社員コード
          </label>
          <input
            id="employeeCode"
            type="text"
            value={employeeCode}
            onChange={(e) => setEmployeeCode(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md"
            required
          />
        </div>
      )}

      <div>
        <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-1">
          氏名
        </label>
        <input
          id="name"
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="w-full px-3 py-2 border border-gray-300 rounded-md"
          required
        />
      </div>

      <div>
        <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
          メールアドレス
        </label>
        <input
          id="email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="w-full px-3 py-2 border border-gray-300 rounded-md"
          required
        />
      </div>

      {showPassword && (
        <div>
          <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
            パスワード（8文字以上）
          </label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md"
            required
            minLength={8}
          />
        </div>
      )}

      <div>
        <label htmlFor="role" className="block text-sm font-medium text-gray-700 mb-1">
          ロール
        </label>
        <select
          id="role"
          value={role}
          onChange={(e) => setRole(e.target.value)}
          className="w-full px-3 py-2 border border-gray-300 rounded-md"
        >
          <option value="EMPLOYEE">一般社員</option>
          <option value="APPROVER">承認者</option>
          <option value="HR">人事</option>
        </select>
      </div>

      <button
        type="submit"
        disabled={loading}
        className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 disabled:opacity-50"
      >
        {loading ? "処理中..." : submitLabel}
      </button>
    </form>
  );
}
