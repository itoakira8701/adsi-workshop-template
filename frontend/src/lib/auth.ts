import { fetchApi } from "./api-client";

export interface Employee {
  id: number;
  employeeCode: string;
  name: string;
  email: string;
  role: "EMPLOYEE" | "APPROVER" | "HR";
  active: boolean;
}

interface LoginResponse {
  token: string;
  employee: Employee;
}

export async function login(
  employeeCode: string,
  password: string
): Promise<Employee> {
  const response = await fetchApi<LoginResponse>("/auth/login", {
    method: "POST",
    body: JSON.stringify({ employeeCode, password }),
  });

  localStorage.setItem("token", response.token);
  localStorage.setItem("employee", JSON.stringify(response.employee));

  return response.employee;
}

export function logout(): void {
  localStorage.removeItem("token");
  localStorage.removeItem("employee");
  window.location.href = "/login";
}

export function getStoredEmployee(): Employee | null {
  if (typeof window === "undefined") return null;
  const stored = localStorage.getItem("employee");
  if (!stored) return null;
  return JSON.parse(stored);
}

export function getToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem("token");
}

export function isAuthenticated(): boolean {
  return getToken() !== null;
}
