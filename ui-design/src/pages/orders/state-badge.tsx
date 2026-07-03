import { Badge } from "@/components/ui/badge";
import { ORDER_STATE_LABELS, type OrderState } from "@/lib/types";
import { cn } from "@/lib/utils";

// Colores translúcidos por estado (sin gradientes decorativos).
const STATE_BADGE_STYLES: Record<OrderState, string> = {
  IN_CONFIRMATION:
    "border-amber-500/30 bg-amber-500/15 text-amber-700 dark:bg-amber-500/20 dark:text-amber-400",
  PENDING_PAYMENT:
    "border-orange-500/30 bg-orange-500/15 text-orange-700 dark:bg-orange-500/20 dark:text-orange-400",
  PAID: "border-teal-500/30 bg-teal-500/15 text-teal-700 dark:bg-teal-500/20 dark:text-teal-400",
  PREPARATION:
    "border-blue-500/30 bg-blue-500/15 text-blue-700 dark:bg-blue-500/20 dark:text-blue-400",
  SHIPPED:
    "border-violet-500/30 bg-violet-500/15 text-violet-700 dark:bg-violet-500/20 dark:text-violet-400",
  READY_FOR_PICKUP:
    "border-cyan-500/30 bg-cyan-500/15 text-cyan-700 dark:bg-cyan-500/20 dark:text-cyan-400",
  COMPLETED:
    "border-emerald-500/30 bg-emerald-500/15 text-emerald-700 dark:bg-emerald-500/20 dark:text-emerald-400",
};

const STATE_DOT_STYLES: Record<OrderState, string> = {
  IN_CONFIRMATION: "bg-amber-500",
  PENDING_PAYMENT: "bg-orange-500",
  PAID: "bg-teal-500",
  PREPARATION: "bg-blue-500",
  SHIPPED: "bg-violet-500",
  READY_FOR_PICKUP: "bg-cyan-500",
  COMPLETED: "bg-emerald-500",
};

interface StateBadgeProps {
  state: OrderState;
  className?: string;
}

/** Badge reutilizable de estado de pedido: color + etiqueta en español consistentes. */
export function StateBadge({ state, className }: StateBadgeProps) {
  return (
    <Badge variant="outline" className={cn("gap-1.5", STATE_BADGE_STYLES[state], className)}>
      <span className={cn("size-1.5 rounded-full", STATE_DOT_STYLES[state])} aria-hidden="true" />
      {ORDER_STATE_LABELS[state]}
    </Badge>
  );
}
