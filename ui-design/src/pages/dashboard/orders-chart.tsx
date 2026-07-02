import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import type { Order } from "@/lib/types";
import { ORDER_STATE_LABELS, ORDER_STATES } from "@/lib/types";

function formatDay(date: Date): string {
  return date.toLocaleDateString("es-CO", { day: "2-digit", month: "short" });
}

function lastSevenDaysData(orders: Order[]) {
  const days = Array.from({ length: 7 }, (_, index) => {
    const date = new Date();
    date.setHours(0, 0, 0, 0);
    date.setDate(date.getDate() - (6 - index));
    return { key: date.toISOString().slice(0, 10), label: formatDay(date), pedidos: 0 };
  });
  const byKey = new Map(days.map((day) => [day.key, day]));

  for (const order of orders) {
    const key = new Date(order.orderDate).toISOString().slice(0, 10);
    const day = byKey.get(key);
    if (day) day.pedidos += 1;
  }

  return days;
}

export function OrdersChart({ orders }: { orders: Order[] }) {
  const byState = ORDER_STATES.map((state) => ({
    estado: ORDER_STATE_LABELS[state],
    pedidos: orders.filter((order) => order.state === state).length,
  }));
  const byDay = lastSevenDaysData(orders);

  return (
    <div className="grid grid-cols-1 gap-4 xl:grid-cols-2">
      <Card className="glass rounded-2xl">
        <CardHeader>
          <CardTitle>Pedidos por estado</CardTitle>
        </CardHeader>
        <CardContent className="h-64">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={byState}>
              <CartesianGrid stroke="var(--border)" strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="estado" tick={{ fontSize: 11 }} tickLine={false} axisLine={false} />
              <YAxis
                allowDecimals={false}
                tick={{ fontSize: 11 }}
                tickLine={false}
                axisLine={false}
              />
              <Tooltip
                cursor={{ fill: "rgba(0,0,0,0.04)" }}
                contentStyle={{
                  background: "var(--popover)",
                  border: "1px solid var(--border)",
                  borderRadius: "14px",
                }}
              />
              <Bar dataKey="pedidos" radius={[12, 12, 4, 4]} fill="var(--primary)" />
            </BarChart>
          </ResponsiveContainer>
        </CardContent>
      </Card>

      <Card className="glass rounded-2xl">
        <CardHeader>
          <CardTitle>Actividad últimos 7 días</CardTitle>
        </CardHeader>
        <CardContent className="h-64">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={byDay}>
              <defs>
                <linearGradient id="orders-area" x1="0" x2="0" y1="0" y2="1">
                  <stop offset="0%" stopColor="var(--primary)" stopOpacity={0.24} />
                  <stop offset="100%" stopColor="var(--primary)" stopOpacity={0.02} />
                </linearGradient>
              </defs>
              <CartesianGrid stroke="var(--border)" strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="label" tick={{ fontSize: 11 }} tickLine={false} axisLine={false} />
              <YAxis
                allowDecimals={false}
                tick={{ fontSize: 11 }}
                tickLine={false}
                axisLine={false}
              />
              <Tooltip
                contentStyle={{
                  background: "var(--popover)",
                  border: "1px solid var(--border)",
                  borderRadius: "14px",
                }}
              />
              <Area
                type="monotone"
                dataKey="pedidos"
                stroke="var(--primary)"
                strokeWidth={2}
                fill="url(#orders-area)"
              />
            </AreaChart>
          </ResponsiveContainer>
        </CardContent>
      </Card>
    </div>
  );
}
