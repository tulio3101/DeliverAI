import {
  CircleCheck,
  CircleHelp,
  CircleX,
  Database,
  type LucideIcon,
  Palette,
  ServerCog,
} from "lucide-react";
import { useTheme } from "next-themes";
import { useEffect, useState } from "react";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import { useBackendStatus } from "@/hooks/use-backend-status";
import { API_BASE_URL, MOCK_DATA } from "@/lib/config";
import type { BackendStatus } from "@/lib/types";

const THEME_OPTIONS: Array<{ value: string; label: string }> = [
  { value: "light", label: "Claro" },
  { value: "dark", label: "Oscuro" },
  { value: "system", label: "Sistema" },
];

const BACKEND_GAPS: string[] = [
  "Listado de productos: el backend no expone GET /products; la sección de Productos debe manejar este caso sin romper.",
  "Sin integración de n8n / WhatsApp todavía: los pedidos no se notifican por esos canales.",
  "Admin UI en MVP: esta aplicación es parte del alcance mínimo; algunas operaciones pueden no tener endpoint disponible aún.",
];

const STATUS_META: Record<
  BackendStatus,
  { label: string; icon: LucideIcon; variant: "secondary" | "destructive" }
> = {
  online: { label: "En línea", icon: CircleCheck, variant: "secondary" },
  offline: { label: "Sin conexión", icon: CircleX, variant: "destructive" },
  unknown: { label: "Verificando…", icon: CircleHelp, variant: "secondary" },
};

export default function SettingsPage() {
  return (
    <div className="mx-auto flex w-full max-w-5xl flex-col gap-4 p-4 sm:p-6">
      <header>
        <h1 className="font-heading text-lg font-semibold">Ajustes</h1>
        <p className="text-xs text-muted-foreground">
          Configuración y estado operativo de DeliverAI Admin.
        </p>
      </header>

      <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
        <DataModeCard />
        <AppearanceCard />
        <SystemStatusCard />
      </div>
    </div>
  );
}

function DataModeCard() {
  return (
    <Card className="glass rounded-2xl">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Database className="size-4" aria-hidden="true" /> Modo de datos
        </CardTitle>
        <CardDescription>Origen de los datos que consume esta aplicación.</CardDescription>
      </CardHeader>
      <CardContent className="flex flex-col gap-3">
        <div className="flex items-center justify-between gap-2">
          <span className="text-muted-foreground">VITE_MOCK_DATA</span>
          <Badge variant="secondary">
            {MOCK_DATA ? "true (datos demo)" : "false (backend real)"}
          </Badge>
        </div>
        <div className="flex items-center justify-between gap-2">
          <span className="text-muted-foreground">VITE_API_BASE_URL</span>
          <code className="rounded bg-muted px-1.5 py-0.5 text-[0.7rem]">{API_BASE_URL}</code>
        </div>

        <Separator />

        <p className="text-muted-foreground">
          Estos valores son de solo lectura aquí: se definen en{" "}
          <code className="rounded bg-muted px-1 py-0.5">.env</code> (variables{" "}
          <code className="rounded bg-muted px-1 py-0.5">VITE_MOCK_DATA</code> y{" "}
          <code className="rounded bg-muted px-1 py-0.5">VITE_API_BASE_URL</code>) y solo toman
          efecto tras reiniciar el servidor de desarrollo.
        </p>
        <p className="text-muted-foreground">
          Vite únicamente expone al cliente las variables con prefijo{" "}
          <code className="rounded bg-muted px-1 py-0.5">VITE_*</code>; cualquier otra variable de
          entorno no estará disponible en esta aplicación.
        </p>
      </CardContent>
    </Card>
  );
}

function AppearanceCard() {
  const { theme, setTheme } = useTheme();
  const [mounted, setMounted] = useState(false);

  useEffect(() => setMounted(true), []);

  return (
    <Card className="glass rounded-2xl">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Palette className="size-4" aria-hidden="true" /> Apariencia
        </CardTitle>
        <CardDescription>Elige cómo se ve la interfaz.</CardDescription>
      </CardHeader>
      <CardContent className="flex flex-col gap-3">
        <div className="flex items-center justify-between gap-2">
          <Label htmlFor="theme-select">Tema</Label>
          {mounted ? (
            <Select value={theme ?? "system"} onValueChange={setTheme}>
              <SelectTrigger id="theme-select" className="w-36" aria-label="Tema de la interfaz">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {THEME_OPTIONS.map((opt) => (
                  <SelectItem key={opt.value} value={opt.value}>
                    {opt.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          ) : (
            <Skeleton className="h-7 w-36" />
          )}
        </div>
        <p className="text-muted-foreground">
          &quot;Sistema&quot; sigue la preferencia de tu sistema operativo y se ajusta
          automáticamente.
        </p>
      </CardContent>
    </Card>
  );
}

function SystemStatusCard() {
  const status = useBackendStatus();
  const meta = STATUS_META[status];
  const StatusIcon = meta.icon;

  return (
    <Card className="glass rounded-2xl lg:col-span-2">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <ServerCog className="size-4" aria-hidden="true" /> Estado del sistema
        </CardTitle>
        <CardDescription>Conectividad con el backend y brechas conocidas del MVP.</CardDescription>
      </CardHeader>
      <CardContent className="flex flex-col gap-4">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div className="flex items-center gap-2">
            <span className="text-muted-foreground">Backend</span>
            <Badge variant={meta.variant} className="gap-1">
              <StatusIcon className="size-2.5" aria-hidden="true" />
              {meta.label}
            </Badge>
            {MOCK_DATA && (
              <span className="text-muted-foreground">
                (modo demo: no se verifica un backend real)
              </span>
            )}
          </div>
          <div className="flex items-center gap-2">
            <span className="text-muted-foreground">URL API</span>
            <code className="rounded bg-muted px-1.5 py-0.5 text-[0.7rem]">{API_BASE_URL}</code>
          </div>
        </div>

        <Separator />

        <div className="flex flex-col gap-2">
          <p className="font-medium text-foreground">Brechas conocidas del backend</p>
          <ul className="flex flex-col gap-1.5">
            {BACKEND_GAPS.map((gap) => (
              <li key={gap} className="flex gap-2 text-muted-foreground">
                <span aria-hidden="true">•</span>
                <span>{gap}</span>
              </li>
            ))}
          </ul>
        </div>
      </CardContent>
    </Card>
  );
}
