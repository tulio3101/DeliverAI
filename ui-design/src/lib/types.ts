// Tipos alineados con los DTOs reales del backend Spring Boot (DeliverAI).
// Fuente: src/main/java/edu/eci/ahia/model/dto — no inventar campos.

export type OrderState =
  | "IN_CONFIRMATION"
  | "PENDING_PAYMENT"
  | "PAID"
  | "PREPARATION"
  | "SHIPPED"
  | "READY_FOR_PICKUP"
  | "COMPLETED";

// Orden lógico del flujo para la UI (el backend guarda ORDINAL en otro orden).
export const ORDER_STATES: OrderState[] = [
  "IN_CONFIRMATION",
  "PENDING_PAYMENT",
  "PAID",
  "PREPARATION",
  "SHIPPED",
  "READY_FOR_PICKUP",
  "COMPLETED",
];

export const ORDER_STATE_LABELS: Record<OrderState, string> = {
  IN_CONFIRMATION: "Ordenado",
  PENDING_PAYMENT: "Por pagar",
  PAID: "Pagado",
  PREPARATION: "En preparación",
  SHIPPED: "Enviado",
  READY_FOR_PICKUP: "Listo para recoger",
  COMPLETED: "Completado",
};

export interface Product {
  id: number;
  name: string;
  units: number;
  price: number;
}

export interface ProductCreateInput {
  name: string;
  units: number;
  price: number;
}

export interface User {
  id: number;
  name: string;
  email: string | null;
  phoneNumber: number | null;
}

export interface UserInput {
  name: string;
  email?: string;
  phoneNumber?: number;
}

export interface OrderItem {
  id: number;
  product: Product;
  quantity: number;
  // Detalle del pastel capturado por el agente. Opcional: el backend real los
  // devuelve; los datos demo (mock) no los incluyen.
  flavor?: string;
  filling?: string;
  servings?: number;
  decoration?: string;
  referenceImageUrl?: string | null;
}

export interface Order {
  id: number;
  orderDate: string; // ISO LocalDateTime
  state: OrderState;
  subTotal: number;
  user: User | null;
  orderItems: OrderItem[];
  // Entrega y notas capturadas por el agente (backend real; ausentes en mock).
  deliveryDate?: string; // ISO LocalDate
  deliveryAddress?: string | null;
  notes?: string | null;
}

export interface OrderItemInput {
  productId: number;
  quantity: number;
}

export interface OrderCreateInput {
  subTotal: number;
  orderItems: OrderItemInput[];
}

// ── Errores y estado de conexión ──────────────────────────────

export type ApiErrorKind =
  | "network" // backend caído / CORS / DNS
  | "timeout"
  | "http" // 4xx / 5xx
  | "unsupported"; // endpoint no existe en el backend actual (gap)

export class ApiError extends Error {
  kind: ApiErrorKind;
  status?: number;

  constructor(kind: ApiErrorKind, message: string, status?: number) {
    super(message);
    this.kind = kind;
    this.status = status;
  }
}

export type BackendStatus = "unknown" | "online" | "offline";

export type DataMode = "mock" | "api";
