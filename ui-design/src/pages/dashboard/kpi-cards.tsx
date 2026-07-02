import type { LucideIcon } from "lucide-react";
import { CheckCircle2, Clock3, CreditCard, PackageCheck } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { formatMoney } from "@/lib/format";
import type { Order } from "@/lib/types";

interface Kpi {
  label: string;
  value: string;
  detail: string;
  icon: LucideIcon;
}

function isToday(iso: string): boolean {
  const date = new Date(iso);
  const now = new Date();
  return date.toDateString() === now.toDateString();
}

export function KpiCards({ orders }: { orders: Order[] }) {
  const inConfirmation = orders.filter((order) => order.state === "IN_CONFIRMATION").length;
  const inPreparation = orders.filter((order) => order.state === "PREPARATION").length;
  const completedToday = orders.filter(
    (order) => order.state === "COMPLETED" && isToday(order.orderDate),
  );
  const salesToday = completedToday.reduce((total, order) => total + order.subTotal, 0);

  const kpis: Kpi[] = [
    {
      label: "En confirmación",
      value: String(inConfirmation),
      detail: "requieren validación",
      icon: Clock3,
    },
    {
      label: "En preparación",
      value: String(inPreparation),
      detail: "en cocina u operación",
      icon: PackageCheck,
    },
    {
      label: "Completados hoy",
      value: String(completedToday.length),
      detail: "cerrados en la fecha",
      icon: CheckCircle2,
    },
    {
      label: "Ventas del día",
      value: formatMoney(salesToday),
      detail: "solo pedidos completados",
      icon: CreditCard,
    },
  ];

  return (
    <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-4">
      {kpis.map(({ label, value, detail, icon: Icon }) => (
        <Card key={label} className="glass rounded-2xl">
          <CardContent className="flex items-start justify-between gap-3 p-4">
            <div className="min-w-0">
              <p className="text-muted-foreground">{label}</p>
              <p className="mt-2 truncate font-heading text-2xl font-semibold tabular-nums">
                {value}
              </p>
              <p className="mt-1 text-[0.7rem] text-muted-foreground">{detail}</p>
            </div>
            <div className="flex size-9 shrink-0 items-center justify-center rounded-2xl bg-primary/10 text-primary">
              <Icon className="size-4" aria-hidden="true" />
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}
