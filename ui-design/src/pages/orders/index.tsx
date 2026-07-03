import { AlertCircle, Eye, RefreshCw, Search, ShoppingBag } from "lucide-react";
import { useMemo, useState } from "react";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { useAsyncData } from "@/hooks/use-async-data";
import { api } from "@/lib/api";
import { MOCK_DATA } from "@/lib/config";
import { formatDateTime, formatMoney } from "@/lib/format";
import type { Order, OrderState } from "@/lib/types";
import { ORDER_STATE_LABELS, ORDER_STATES } from "@/lib/types";
import { OrderDetailSheet } from "@/pages/orders/order-detail-sheet";
import { StateBadge } from "@/pages/orders/state-badge";

type TabValue = "all" | OrderState;

const TABS: Array<{ value: TabValue; label: string }> = [
  { value: "all", label: "Todos" },
  ...ORDER_STATES.map((state) => ({ value: state, label: ORDER_STATE_LABELS[state] })),
];

const SKELETON_ROWS = ["a", "b", "c", "d", "e"];

function listOrdersByTab(tab: TabValue): Promise<Order[]> {
  return tab === "all" ? api.listOrders() : api.listOrdersByState(tab);
}

function matchesOrder(order: Order, query: string): boolean {
  const normalized = query.trim().toLowerCase();
  if (!normalized) return true;
  return [String(order.id), order.user?.name ?? ""].some((value) =>
    value.toLowerCase().includes(normalized),
  );
}

export default function OrdersPage() {
  const [activeTab, setActiveTab] = useState<TabValue>("all");
  const [search, setSearch] = useState("");
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [sheetOpen, setSheetOpen] = useState(false);
  const { data, loading, error, reload } = useAsyncData(
    () => listOrdersByTab(activeTab),
    [activeTab],
  );

  const filtered = useMemo(
    () => (data ?? []).filter((order) => matchesOrder(order, search)),
    [data, search],
  );

  function openDetail(order: Order) {
    setSelectedOrder(order);
    setSheetOpen(true);
  }

  return (
    <>
      <div className="mx-auto flex w-full max-w-7xl flex-col gap-4 p-4 sm:p-6">
        <header className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
          <div className="min-w-0">
            <div className="flex flex-wrap items-center gap-2">
              <h2 className="font-heading text-xl font-semibold">Pedidos</h2>
              {MOCK_DATA && <Badge variant="secondary">Datos demo</Badge>}
            </div>
            <p className="text-muted-foreground">Filtra, revisa detalle y cambia estados.</p>
          </div>
        </header>

        <Card className="glass rounded-2xl">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <ShoppingBag className="size-4" aria-hidden="true" /> Lista de pedidos
            </CardTitle>
            <CardDescription>
              {data ? `${data.length} pedido${data.length === 1 ? "" : "s"}` : "Cargando…"}
            </CardDescription>
          </CardHeader>
          <CardContent className="flex flex-col gap-4">
            <div className="flex flex-col gap-3 xl:flex-row xl:items-center xl:justify-between">
              <Tabs value={activeTab} onValueChange={(value) => setActiveTab(value as TabValue)}>
                <TabsList className="glass grid w-full grid-cols-4 bg-background/40 xl:inline-flex xl:w-fit">
                  {TABS.map((tab) => (
                    <TabsTrigger key={tab.value} value={tab.value}>
                      {tab.label}
                    </TabsTrigger>
                  ))}
                </TabsList>
              </Tabs>

              <div className="relative w-full xl:w-72">
                <Search
                  className="pointer-events-none absolute top-1/2 left-2 size-3.5 -translate-y-1/2 text-muted-foreground"
                  aria-hidden="true"
                />
                <Input
                  value={search}
                  onChange={(event) => setSearch(event.target.value)}
                  placeholder="Buscar por ID o cliente…"
                  className="pl-7"
                  aria-label="Buscar pedidos"
                  disabled={loading || !!error}
                />
              </div>
            </div>

            {loading && (
              <Table>
                <TableBody>
                  {SKELETON_ROWS.map((key) => (
                    <TableRow key={key}>
                      <TableCell colSpan={7}>
                        <Skeleton className="h-6 w-full" />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
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

            {!loading && !error && filtered.length === 0 && (
              <div className="flex flex-col items-center gap-2 py-10 text-center text-muted-foreground">
                <ShoppingBag className="size-8" aria-hidden="true" />
                <p>No hay pedidos para este filtro.</p>
              </div>
            )}

            {!loading && !error && filtered.length > 0 && (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>ID</TableHead>
                    <TableHead>Fecha</TableHead>
                    <TableHead>Cliente</TableHead>
                    <TableHead>Items</TableHead>
                    <TableHead>Total</TableHead>
                    <TableHead>Estado</TableHead>
                    <TableHead className="text-right">Acciones</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filtered.map((order) => (
                    <TableRow
                      key={order.id}
                      className="cursor-pointer"
                      onClick={() => openDetail(order)}
                    >
                      <TableCell className="font-medium">#{order.id}</TableCell>
                      <TableCell>{formatDateTime(order.orderDate)}</TableCell>
                      <TableCell>{order.user?.name ?? "—"}</TableCell>
                      <TableCell>{order.orderItems.length}</TableCell>
                      <TableCell className="font-medium tabular-nums">
                        {formatMoney(order.subTotal)}
                      </TableCell>
                      <TableCell>
                        <StateBadge state={order.state} />
                      </TableCell>
                      <TableCell className="text-right">
                        <Button
                          type="button"
                          variant="ghost"
                          size="icon-sm"
                          aria-label={`Ver pedido ${order.id}`}
                          onClick={(event) => {
                            event.stopPropagation();
                            openDetail(order);
                          }}
                        >
                          <Eye aria-hidden="true" />
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </CardContent>
        </Card>
      </div>

      <OrderDetailSheet
        order={selectedOrder}
        open={sheetOpen}
        onOpenChange={setSheetOpen}
        onChanged={reload}
      />
    </>
  );
}
