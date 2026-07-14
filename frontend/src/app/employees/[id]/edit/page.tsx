"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import AuthGuard from "@/components/AuthGuard";
import Navigation from "@/components/Navigation";
import EmployeeForm, { EmployeeFormData } from "@/components/EmployeeForm";
import { fetchApi } from "@/lib/api-client";
import { Employee } from "@/lib/auth";

interface EmployeeDetail extends Employee {
  version: number;
}

export default function EditEmployeePage() {
  return (
    <AuthGuard>
      <Navigation />
      <main className="flex-1 p-6">
        <EditEmployeeContent />
      </main>
    </AuthGuard>
  );
}

function EditEmployeeContent() {
  const params = useParams();
  const router = useRouter();
  const [employee, setEmployee] = useState<EmployeeDetail | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const data = await fetchApi<EmployeeDetail>(`/employees/${params.id}`);
        setEmployee(data);
      } catch {
        alert("社員情報の取得に失敗しました");
        router.push("/employees");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [params.id, router]);

  const handleSubmit = async (data: EmployeeFormData) => {
    await fetchApi(`/employees/${params.id}`, {
      method: "PUT",
      body: JSON.stringify({ ...data, version: employee?.version }),
    });
    router.push("/employees");
  };

  if (loading) return <p>読み込み中...</p>;
  if (!employee) return null;

  return (
    <div className="max-w-lg mx-auto">
      <h1 className="text-2xl font-bold mb-6">社員編集</h1>
      <EmployeeForm
        initialData={{ name: employee.name, email: employee.email, role: employee.role }}
        onSubmit={handleSubmit}
        submitLabel="更新"
      />
    </div>
  );
}
