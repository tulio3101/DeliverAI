import type { DataAdapter } from "@/lib/api/data-adapter";
import { http } from "@/lib/api/http";
import {
  ApiError,
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
 * Endpoints según project/api-inventory.md del harness — no inventar rutas.
 *
 * Gap conocido del backend actual: NO existe GET de listado de productos.
 * listProducts lanza ApiError("unsupported") y la UI debe mostrar el gap.
 */
export const backendAdapter: DataAdapter = {
  mode: "api",

  // ── Products ──
  listProducts(): Promise<Product[]> {
    return Promise.reject(
      new ApiError(
        "unsupported",
        "El backend actual no expone GET /products (listado). Gap documentado en el harness.",
      ),
    );
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
    // No hay GET /order (todas); se compone desde los 3 estados soportados.
    const states: OrderState[] = ["IN_CONFIRMATION", "PREPARATION", "COMPLETED"];
    const results = await Promise.all(
      states.map((state) => http<Order[]>("/order/state", { params: { state } })),
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
