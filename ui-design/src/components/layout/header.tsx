import { Menu, Server, ServerOff } from "lucide-react";
import { useMemo, useState } from "react";
import { useLocation } from "react-router-dom";
import { SidebarModeBadge, SidebarNav } from "@/components/layout/sidebar";
import { ThemeToggle } from "@/components/layout/theme-toggle";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetTrigger } from "@/components/ui/sheet";
import type { BackendStatus } from "@/lib/types";

const TITLE_BY_PATH: Record<string, string> = {
  "/": "Dashboard",
  "/pedidos": "Pedidos",
  "/productos": "Productos",
  "/clientes": "Clientes",
  "/ajustes": "Ajustes",
};

const STATUS_LABELS: Record<BackendStatus, string> = {
  unknown: "Verificando",
  online: "Backend en línea",
  offline: "Backend offline",
};

export function Header({ status }: { status: BackendStatus }) {
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);

  const title = useMemo(() => TITLE_BY_PATH[location.pathname] ?? "DeliverAI", [location.pathname]);
  const StatusIcon = status === "offline" ? ServerOff : Server;

  return (
    <header className="glass-strong sticky top-0 z-30 flex min-h-14 items-center justify-between gap-3 rounded-none border-x-0 border-t-0 px-4 py-3 lg:rounded-bl-2xl">
      <div className="flex min-w-0 items-center gap-2">
        <Sheet open={mobileOpen} onOpenChange={setMobileOpen}>
          <SheetTrigger asChild>
            <Button
              type="button"
              variant="ghost"
              size="icon"
              aria-label="Abrir navegación"
              className="lg:hidden"
            >
              <Menu />
            </Button>
          </SheetTrigger>
          <SheetContent side="left" className="glass-strong w-72" showCloseButton>
            <SheetHeader>
              <SheetTitle>DeliverAI Admin</SheetTitle>
            </SheetHeader>
            <div className="flex flex-1 flex-col gap-4 px-4 pb-4">
              <SidebarNav onNavigate={() => setMobileOpen(false)} />
              <div className="mt-auto">
                <SidebarModeBadge />
              </div>
            </div>
          </SheetContent>
        </Sheet>

        <div className="min-w-0">
          <p className="truncate text-[0.68rem] uppercase tracking-[0.18em] text-muted-foreground">
            DeliverAI Admin
          </p>
          <h1 className="truncate font-heading text-base font-semibold">{title}</h1>
        </div>
      </div>

      <div className="flex shrink-0 items-center gap-2">
        <Badge
          variant={status === "offline" ? "destructive" : "secondary"}
          className="hidden gap-1 sm:inline-flex"
        >
          <StatusIcon className="size-3" aria-hidden="true" />
          {STATUS_LABELS[status]}
        </Badge>
        <ThemeToggle />
      </div>
    </header>
  );
}
