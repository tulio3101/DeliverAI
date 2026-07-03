import { useEffect, useRef } from "react";
import { toast } from "sonner";
import { api } from "@/lib/api";
import type { Order, OrderState } from "@/lib/types";
import { ORDER_STATE_LABELS } from "@/lib/types";

const POLL_MS = 10_000;

/**
 * Notificaciones de pedidos vía polling REST (el backend no expone
 * SSE/WebSocket, así que no hay alternativa a sondear).
 *
 * Cada ciclo compara el snapshot nuevo contra el anterior:
 * - pedidos que no existían antes -> toast "Nuevo pedido #id".
 * - pedidos cuyo estado cambió -> toast "Pedido #id: A -> B".
 * La primera carga solo guarda el snapshot base, sin toasts.
 * Los errores de polling se silencian: el banner de backend-status
 * ya informa cuando el backend está offline.
 */
export function useOrderNotifications(): void {
  const previousStates = useRef<Map<number, OrderState> | null>(null);

  useEffect(() => {
    let cancelled = false;

    const poll = async () => {
      if (document.hidden) return;

      let orders: Order[];
      try {
        orders = await api.listOrders();
      } catch {
        return;
      }
      if (cancelled) return;

      const previous = previousStates.current;
      const next = new Map<number, OrderState>();

      for (const order of orders) {
        next.set(order.id, order.state);

        if (previous === null) continue;

        const prevState = previous.get(order.id);
        if (prevState === undefined) {
          toast.info(`Nuevo pedido #${order.id}`);
        } else if (prevState !== order.state) {
          toast.info(
            `Pedido #${order.id}: ${ORDER_STATE_LABELS[prevState]} -> ${ORDER_STATE_LABELS[order.state]}`,
          );
        }
      }

      previousStates.current = next;
    };

    poll();
    const timer = setInterval(poll, POLL_MS);
    return () => {
      cancelled = true;
      clearInterval(timer);
    };
  }, []);
}
