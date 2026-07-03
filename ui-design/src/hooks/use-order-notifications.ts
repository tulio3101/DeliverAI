import { useEffect, useRef } from "react";
import { toast } from "sonner";
import { api } from "@/lib/api";
import type { Order, OrderState } from "@/lib/types";
import { ORDER_STATE_LABELS } from "@/lib/types";

const POLL_MS = 10_000;

/**
 * Order notifications via REST polling (the backend exposes no
 * SSE/WebSocket, so polling is the only option).
 *
 * Each cycle compares the new snapshot against the previous one:
 * - orders that did not exist before -> toast "Nuevo pedido #id".
 * - orders whose state changed -> toast "Pedido #id: A -> B".
 * The first load only stores the base snapshot, no toasts.
 * Polling errors are silenced: the backend-status banner already
 * informs when the backend is offline.
 * Toast texts stay in Spanish to match the rest of the UI language.
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
