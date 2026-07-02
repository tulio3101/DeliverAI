import { Trash2 } from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import { api } from "@/lib/api";
import { formatDateTime, formatMoney } from "@/lib/format";
import type { Order, OrderState } from "@/lib/types";
import { ORDER_STATE_LABELS, ORDER_STATES } from "@/lib/types";
import { StateBadge } from "@/pages/orders/state-badge";

interface OrderDetailSheetProps {
  order: Order | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onChanged: () => void;
}

export function OrderDetailSheet({ order, open, onOpenChange, onChanged }: OrderDetailSheetProps) {
  const [state, setState] = useState<OrderState>("IN_CONFIRMATION");
  const [saving, setSaving] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    if (order) setState(order.state);
  }, [order]);

  const totalItems = useMemo(
    () => order?.orderItems.reduce((total, item) => total + item.quantity, 0) ?? 0,
    [order],
  );

  if (!order) return null;

  async function handleUpdateState() {
    if (!order) return;
    setSaving(true);
    try {
      await api.updateOrderState(order.id, state);
      toast.success(`Pedido #${order.id} actualizado`);
      onChanged();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "No se pudo actualizar el estado.");
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete() {
    if (!order) return;
    setDeleting(true);
    try {
      await api.deleteOrder(order.id);
      toast.success(`Pedido #${order.id} eliminado`);
      setDeleteOpen(false);
      onOpenChange(false);
      onChanged();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "No se pudo eliminar el pedido.");
    } finally {
      setDeleting(false);
    }
  }

  return (
    <>
      <Sheet open={open} onOpenChange={onOpenChange}>
        <SheetContent side="right" className="glass-strong overflow-y-auto sm:max-w-lg">
          <SheetHeader>
            <SheetTitle>Pedido #{order.id}</SheetTitle>
            <SheetDescription>{formatDateTime(order.orderDate)}</SheetDescription>
          </SheetHeader>

          <div className="flex flex-col gap-5 px-6 pb-6">
            <section className="rounded-2xl border border-border/60 bg-background/35 p-4">
              <div className="mb-3 flex items-center justify-between gap-3">
                <p className="font-medium">Estado</p>
                <StateBadge state={order.state} />
              </div>
              <div className="flex flex-col gap-2 sm:flex-row">
                <Select value={state} onValueChange={(value) => setState(value as OrderState)}>
                  <SelectTrigger className="w-full" aria-label="Nuevo estado del pedido">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {ORDER_STATES.map((item) => (
                      <SelectItem key={item} value={item}>
                        {ORDER_STATE_LABELS[item]}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <Button onClick={handleUpdateState} disabled={saving || state === order.state}>
                  {saving ? "Actualizando…" : "Actualizar estado"}
                </Button>
              </div>
            </section>

            <section className="grid grid-cols-1 gap-3 sm:grid-cols-2">
              <div className="rounded-2xl border border-border/60 bg-background/35 p-4">
                <p className="text-muted-foreground">Cliente</p>
                <p className="mt-1 font-medium">{order.user?.name ?? "Sin cliente asignado"}</p>
                <p className="text-muted-foreground">{order.user?.email ?? "—"}</p>
                <p className="text-muted-foreground">{order.user?.phoneNumber ?? "—"}</p>
              </div>
              <div className="rounded-2xl border border-border/60 bg-background/35 p-4">
                <p className="text-muted-foreground">Resumen</p>
                <p className="mt-1 font-medium">{totalItems} unidades</p>
                <p className="font-heading text-xl font-semibold tabular-nums">
                  {formatMoney(order.subTotal)}
                </p>
              </div>
            </section>

            <section className="rounded-2xl border border-border/60 bg-background/35 p-4">
              <p className="mb-3 font-medium">Items</p>
              <div className="flex flex-col divide-y divide-border/60">
                {order.orderItems.map((item) => (
                  <div key={item.id} className="grid grid-cols-[1fr_auto] gap-3 py-3">
                    <div className="min-w-0">
                      <p className="truncate font-medium">{item.product.name}</p>
                      <p className="text-muted-foreground">
                        {item.quantity} × {formatMoney(item.product.price)}
                      </p>
                    </div>
                    <p className="font-medium tabular-nums">
                      {formatMoney(item.quantity * item.product.price)}
                    </p>
                  </div>
                ))}
              </div>
            </section>

            <Button variant="destructive" onClick={() => setDeleteOpen(true)}>
              <Trash2 aria-hidden="true" /> Eliminar pedido
            </Button>
          </div>
        </SheetContent>
      </Sheet>

      <Dialog open={deleteOpen} onOpenChange={(next) => !deleting && setDeleteOpen(next)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Eliminar pedido</DialogTitle>
            <DialogDescription>
              ¿Seguro que deseas eliminar el pedido #{order.id}? Esta acción no se puede deshacer.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDeleteOpen(false)} disabled={deleting}>
              Cancelar
            </Button>
            <Button variant="destructive" onClick={handleDelete} disabled={deleting}>
              {deleting ? "Eliminando…" : "Eliminar"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}
