"use client";

import { useMemo } from "react";
import { redirect } from "next/navigation";
import { isAuthenticated } from "@/lib/auth";

export default function AuthGuard({ children }: { children: React.ReactNode }) {
  const authenticated = useMemo(() => {
    if (typeof window === "undefined") return true;
    return isAuthenticated();
  }, []);

  if (!authenticated) {
    redirect("/login");
  }

  return <>{children}</>;
}
