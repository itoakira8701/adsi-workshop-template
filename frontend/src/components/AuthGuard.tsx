"use client";

import { useRouter } from "next/navigation";
import { isAuthenticated } from "@/lib/auth";
import { useSyncExternalStore } from "react";

function subscribe() { return () => {}; }
function getSnapshot() { return isAuthenticated(); }
function getServerSnapshot() { return false; }

export default function AuthGuard({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const authenticated = useSyncExternalStore(subscribe, getSnapshot, getServerSnapshot);

  if (!authenticated) {
    router.replace("/login");
    return null;
  }

  return <>{children}</>;
}
