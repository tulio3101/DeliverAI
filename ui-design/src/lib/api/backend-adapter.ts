import type { DataAdapter } from "@/lib/api/data-adapter";
import { http } from "@/lib/api/http";
import {
  ORDER_STATES,
  type Order,
  type OrderCreateInput,
  type OrderState,
  type Product,
  type ProductCreateInput,
  type User,
  type UserInput,
} from "@/lib/types";

/**
 * Adapter contra el backend Spring real.
 * Endpoints según los controllers de src/main/java/edu/eci/ahia/controller — no inventar rutas.
 */
export const backendAdapter: DataAdapter = {
  mode: "api",

  // ── Products ──
  listProducts(): Promise<Product[]> {
    return http<Product[]>("/products");
  },
  createProduct(input: ProductCreateInput): Promise<Product> {
    return http<Product>("/products", { method: "POST", body: input });
  },
  updateProductPrice(id: number, price: number): Promise<Product> {
    return http<Product>(`/products/${id}/price`, { method: "PATCH", params: { price } });
  },
  updateProductUnits(id: number, units: number): Promise<Product> {
    return http<Product>(`/products/${id}/units`, { method: "PATCH", params: { units } });
  },
  deleteProduct(id: number): Promise<void> {
    return http<void>(`/products/${id}`, { method: "DELETE" });
  },

  // ── Orders ──
  async listOrders(): Promise<Order[]> {
    // No hay GET /order (todas); se compone desde todos los estados soportados.
    const results = await Promise.all(
      ORDER_STATES.map((state) => http<Order[]>("/order/state", { params: { state } })),
    );
    return results.flat();
  },
  getOrder(id: number): Promise<Order> {
    return http<Order>(`/order/${id}`);
  },
  listOrdersByState(state: OrderState): Promise<Order[]> {
    return http<Order[]>("/order/state", { params: { state } });
  },
  listOrdersByUser(userId: number): Promise<Order[]> {
    return http<Order[]>(`/order/user/${userId}`);
  },
  createOrder(input: OrderCreateInput): Promise<Order> {
    return http<Order>("/order", { method: "POST", body: input });
  },
  updateOrderState(id: number, state: OrderState): Promise<Order> {
    return http<Order>(`/order/${id}`, { method: "PATCH", params: { state } });
  },
  deleteOrder(id: number): Promise<void> {
    return http<void>(`/order/${id}`, { method: "DELETE" });
  },

  // ── Users ──
  listUsers(): Promise<User[]> {
    return http<User[]>("/user/all");
  },
  getUser(id: number): Promise<User> {
    return http<User>(`/user/${id}`);
  },
  createUser(input: UserInput): Promise<User> {
    return http<User>("/user", { method: "POST", body: input });
  },
  updateUser(id: number, input: UserInput): Promise<User> {
    return http<User>(`/user/${id}`, { method: "PUT", body: input });
  },
  deleteUser(id: number): Promise<void> {
    return http<void>(`/user/${id}`, { method: "DELETE" });
  },

  // ── Health ──
  async checkHealth(): Promise<boolean> {
    try {
      // Swagger/OpenAPI está siempre habilitado en el backend actual.
      await http<unknown>("/v3/api-docs");
      return true;
    } catch {
      return false;
    }
  },
};
