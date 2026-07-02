import { useEffect, useState } from "react";
import { api } from "@/lib/api";
import { MOCK_DATA } from "@/lib/config";
import type { BackendStatus } from "@/lib/types";

const POLL_MS = 15_000;

/**
 * Estado del backend real. En modo mock siempre "online" (no aplica).
 * En modo API hace ping a /v3/api-docs cada 15s.
 */
export function useBackendStatus(): BackendStatus {
  const [status, setStatus] = useState<BackendStatus>(MOCK_DATA ? "online" : "unknown");

  useEffect(() => {
    if (MOCK_DATA) return;
    let cancelled = false;

    const check = async () => {
      const ok = await api.checkHealth();
      if (!cancelled) setStatus(ok ? "online" : "offline");
    };
    check();
    const timer = setInterval(check, POLL_MS);
    return () => {
      cancelled = true;
      clearInterval(timer);
    };
  }, []);

  return status;
}
