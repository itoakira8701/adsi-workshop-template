"use client";

import { useRouter } from "next/navigation";
import AuthGuard from "@/components/AuthGuard";
import Navigation from "@/components/Navigation";
import EmployeeForm, { EmployeeFormData } from "@/components/EmployeeForm";
import { fetchApi } from "@/lib/api-client";

export default function NewEmployeePage() {
  return (
    <AuthGuard>
      <Navigation />
      <main className="flex-1 p-6">
        <NewEmployeeContent />
      </main>
    </AuthGuard>
  );
}

function NewEmployeeContent() {
  const router = useRouter();

  const handleSubmit = async (data: EmployeeFormData) => {
    await fetchApi("/employees", {
      method: "POST",
      body: JSON.stringify(data),
    });
    router.push("/employees");
  };

  return (
    <div className="max-w-lg mx-auto">
      <h1 className="text-2xl font-bold mb-6">社員登録</h1>
      <EmployeeForm
        showEmployeeCode
        showPassword
        onSubmit={handleSubmit}
        submitLabel="登録"
      />
    </div>
  );
}
