import { ShoppingBag } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { formatMoney, formatRelative } from "@/lib/format";
import type { Order } from "@/lib/types";
import { StateBadge } from "@/pages/orders/state-badge";

export function RecentActivity({ orders }: { orders: Order[] }) {
  const recent = [...orders]
    .sort((a, b) => new Date(b.orderDate).getTime() - new Date(a.orderDate).getTime())
    .slice(0, 6);

  return (
    <Card className="glass rounded-2xl">
      <CardHeader>
        <CardTitle>Actividad reciente</CardTitle>
      </CardHeader>
      <CardContent>
        {recent.length === 0 ? (
          <div className="flex flex-col items-center gap-2 py-8 text-center text-muted-foreground">
            <ShoppingBag className="size-8" aria-hidden="true" />
            <p>No hay pedidos recientes.</p>
          </div>
        ) : (
          <div className="flex flex-col divide-y divide-border/60">
            {recent.map((order) => (
              <div key={order.id} className="flex items-center justify-between gap-3 py-3">
                <div className="min-w-0">
                  <p className="truncate font-medium">
                    #{order.id} · {order.user?.name ?? "Cliente sin asignar"}
                  </p>
                  <p className="text-muted-foreground">{formatRelative(order.orderDate)}</p>
                </div>
                <div className="flex shrink-0 flex-col items-end gap-1">
                  <StateBadge state={order.state} />
                  <span className="font-medium tabular-nums">{formatMoney(order.subTotal)}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
}
