import { AlertCircle, RefreshCw } from "lucide-react";
import { useMemo, useState } from "react";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { useAsyncData } from "@/hooks/use-async-data";
import { api } from "@/lib/api";
import { MOCK_DATA } from "@/lib/config";
import type { Order } from "@/lib/types";
import { KpiCards } from "@/pages/dashboard/kpi-cards";
import { OrdersChart } from "@/pages/dashboard/orders-chart";
import { RecentActivity } from "@/pages/dashboard/recent-activity";

type RangeFilter = "today" | "7d" | "all";

function filterByRange(orders: Order[], range: RangeFilter): Order[] {
  if (range === "all") return orders;
  const now = new Date();
  const start = new Date(now);
  start.setHours(0, 0, 0, 0);
  if (range === "7d") start.setDate(start.getDate() - 6);
  return orders.filter((order) => new Date(order.orderDate) >= start);
}

export default function DashboardPage() {
  const { data, loading, error, reload } = useAsyncData(() => api.listOrders(), []);
  const [range, setRange] = useState<RangeFilter>("today");

  const orders = data ?? [];
  const filteredOrders = useMemo(() => filterByRange(orders, range), [orders, range]);

  return (
    <div className="mx-auto flex w-full max-w-7xl flex-col gap-4 p-4 sm:p-6">
      <header className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div className="min-w-0">
          <div className="flex flex-wrap items-center gap-2">
            <h2 className="font-heading text-xl font-semibold">Control operativo</h2>
            {MOCK_DATA && <Badge variant="secondary">Datos demo</Badge>}
          </div>
          <p className="text-muted-foreground">
            Pedidos, estados, actividad y brechas actuales del MVP.
          </p>
        </div>
        <Select value={range} onValueChange={(value) => setRange(value as RangeFilter)}>
          <SelectTrigger className="w-full sm:w-40" aria-label="Filtrar rango">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="today">Hoy</SelectItem>
            <SelectItem value="7d">7 días</SelectItem>
            <SelectItem value="all">Todo</SelectItem>
          </SelectContent>
        </Select>
      </header>

      {loading && (
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-4">
          {["a", "b", "c", "d"].map((key) => (
            <Skeleton key={key} className="h-28 rounded-2xl" />
          ))}
        </div>
      )}

      {!loading && error && (
        <Alert variant="destructive">
          <AlertCircle aria-hidden="true" />
          <AlertTitle>No se pudieron cargar los pedidos</AlertTitle>
          <AlertDescription>
            <p>{error.message}</p>
            <Button size="sm" variant="outline" className="mt-2" onClick={reload}>
              <RefreshCw aria-hidden="true" /> Reintentar
            </Button>
          </AlertDescription>
        </Alert>
      )}

      {!loading && !error && (
        <>
          <KpiCards orders={filteredOrders} />
          <OrdersChart orders={filteredOrders} />
          <div className="grid grid-cols-1 gap-4 xl:grid-cols-[1fr_0.75fr]">
            <RecentActivity orders={orders} />
            <Card className="glass rounded-2xl">
              <CardHeader>
                <CardTitle>Gaps actuales</CardTitle>
              </CardHeader>
              <CardContent>
                <ul className="flex flex-col gap-2 text-muted-foreground">
                  <li>
                    Estados de pedido limitados a 3 (backend): confirmación, preparación,
                    completado.
                  </li>
                  <li>
                    Pedidos del agente llegan sin tarifar (subtotal en 0 hasta que el admin fija
                    precio).
                  </li>
                  {MOCK_DATA && <li>Modo demo activo: datos locales no reales.</li>}
                </ul>
              </CardContent>
            </Card>
          </div>
        </>
      )}
    </div>
  );
}
