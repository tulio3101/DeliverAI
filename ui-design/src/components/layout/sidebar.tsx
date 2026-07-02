import {
  LayoutDashboard,
  type LucideIcon,
  Package,
  PanelLeftClose,
  PanelLeftOpen,
  Settings,
  ShoppingBag,
  Users,
} from "lucide-react";
import { useState } from "react";
import { NavLink } from "react-router-dom";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { MOCK_DATA } from "@/lib/config";
import { cn } from "@/lib/utils";

export interface NavItem {
  to: string;
  label: string;
  icon: LucideIcon;
}

/** Fuente única de la navegación principal: la usan el sidebar de escritorio y el Sheet móvil del header. */
export const NAV_ITEMS: NavItem[] = [
  { to: "/", label: "Dashboard", icon: LayoutDashboard },
  { to: "/pedidos", label: "Pedidos", icon: ShoppingBag },
  { to: "/productos", label: "Productos", icon: Package },
  { to: "/clientes", label: "Clientes", icon: Users },
  { to: "/ajustes", label: "Ajustes", icon: Settings },
];

interface SidebarNavProps {
  collapsed?: boolean;
  onNavigate?: () => void;
  className?: string;
}

/** Lista de enlaces de navegación, reutilizable entre el sidebar fijo y el Sheet móvil. */
function SidebarNav({ collapsed = false, onNavigate, className }: SidebarNavProps) {
  return (
    <nav aria-label="Navegación principal" className={cn("flex flex-col gap-1", className)}>
      {NAV_ITEMS.map(({ to, label, icon: Icon }) => (
        <NavLink
          key={to}
          to={to}
          end={to === "/"}
          onClick={onNavigate}
          title={collapsed ? label : undefined}
          className={({ isActive }) =>
            cn(
              "flex items-center gap-3 rounded-xl px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-muted/60 hover:text-foreground",
              collapsed && "justify-center px-2",
              isActive && "bg-primary/10 text-primary hover:bg-primary/10 hover:text-primary",
            )
          }
        >
          <Icon className="size-4 shrink-0" aria-hidden="true" />
          <span className={cn("truncate", collapsed && "sr-only")}>{label}</span>
        </NavLink>
      ))}
    </nav>
  );
}

/** Badge de pie de sidebar con el modo de datos activo (demo mock vs. API real). */
function SidebarModeBadge({ collapsed = false }: { collapsed?: boolean }) {
  return (
    <div
      className={cn("flex items-center gap-2", collapsed ? "justify-center" : "justify-between")}
    >
      {!collapsed && <span className="text-[0.625rem] text-muted-foreground">Modo de datos</span>}
      <Badge variant={MOCK_DATA ? "secondary" : "outline"}>{MOCK_DATA ? "Demo" : "API"}</Badge>
    </div>
  );
}

/**
 * Nav lateral fijo de escritorio: glass-strong, colapsable a solo íconos.
 * En mobile permanece oculto; el header abre el mismo nav dentro de un Sheet.
 */
function Sidebar() {
  const [collapsed, setCollapsed] = useState(false);

  return (
    <aside
      aria-label="Barra lateral"
      className={cn(
        "glass-strong sticky top-0 hidden h-dvh shrink-0 flex-col transition-[width] duration-200 ease-in-out lg:flex",
        collapsed ? "w-19" : "w-60",
      )}
    >
      <div
        className={cn(
          "flex items-center gap-2 pt-4 pb-1",
          collapsed ? "justify-center px-2" : "px-4",
        )}
      >
        <div className="flex size-8 shrink-0 items-center justify-center rounded-xl bg-primary font-heading text-sm font-semibold text-primary-foreground">
          D
        </div>
        {!collapsed && (
          <div className="min-w-0 leading-tight">
            <p className="truncate font-heading text-sm font-semibold">DeliverAI</p>
            <p className="truncate text-[0.625rem] text-muted-foreground">Admin</p>
          </div>
        )}
      </div>

      <div className={cn("flex pb-2", collapsed ? "justify-center px-2" : "justify-end px-3")}>
        <Button
          type="button"
          variant="ghost"
          size="icon-sm"
          onClick={() => setCollapsed((value) => !value)}
          aria-label={collapsed ? "Expandir barra lateral" : "Colapsar barra lateral"}
        >
          {collapsed ? (
            <PanelLeftOpen className="size-3.5" />
          ) : (
            <PanelLeftClose className="size-3.5" />
          )}
        </Button>
      </div>

      <Separator className="bg-border/60" />

      <SidebarNav collapsed={collapsed} className="flex-1 overflow-y-auto px-3 py-3" />

      <Separator className="bg-border/60" />

      <div className="px-4 py-3">
        <SidebarModeBadge collapsed={collapsed} />
      </div>
    </aside>
  );
}

export { Sidebar, SidebarModeBadge, SidebarNav };
