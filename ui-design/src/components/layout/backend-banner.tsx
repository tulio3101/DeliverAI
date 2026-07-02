import { AlertTriangle, X } from "lucide-react";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { API_BASE_URL, MOCK_DATA } from "@/lib/config";
import type { BackendStatus } from "@/lib/types";

export function BackendBanner({ status }: { status: BackendStatus }) {
  const [dismissed, setDismissed] = useState(false);

  if (dismissed) return null;
  if (MOCK_DATA) {
    return (
      <div className="mx-4 mt-4 flex items-center gap-3 rounded-2xl border border-amber-500/25 bg-amber-500/12 px-3 py-2 text-amber-900 shadow-sm backdrop-blur-md dark:text-amber-200">
        <AlertTriangle className="size-4 shrink-0" aria-hidden="true" />
        <p className="min-w-0 flex-1 text-xs">
          Modo datos demo activo (VITE_MOCK_DATA=true). Los datos no son reales.
        </p>
        <Button
          type="button"
          variant="ghost"
          size="icon-sm"
          aria-label="Ocultar aviso de datos demo"
          onClick={() => setDismissed(true)}
        >
          <X />
        </Button>
      </div>
    );
  }

  if (status !== "offline") return null;

  return (
    <div className="mx-4 mt-4 flex flex-wrap items-center gap-3 rounded-2xl border border-destructive/25 bg-destructive/12 px-3 py-2 text-destructive shadow-sm backdrop-blur-md">
      <AlertTriangle className="size-4 shrink-0" aria-hidden="true" />
      <p className="min-w-0 flex-1 text-xs">
        Backend no disponible en {API_BASE_URL}. Mostrando estado degradado.
      </p>
      <Button type="button" variant="outline" size="sm" onClick={() => window.location.reload()}>
        Reintentar
      </Button>
    </div>
  );
}
