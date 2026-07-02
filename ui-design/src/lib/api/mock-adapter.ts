import type { DataAdapter } from "@/lib/api/data-adapter";
import { mockOrders, mockProducts, mockUsers } from "@/lib/mock-data";
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

// Estado en memoria (se reinicia al recargar). Todo dato aquí es DEMO.
const products: Product[] = structuredClone(mockProducts);
const users: User[] = structuredClone(mockUsers);
const orders: Order[] = structuredClone(mockOrders);

let nextProductId = Math.max(...products.map((p) => p.id)) + 1;
let nextUserId = Math.max(...users.map((u) => u.id)) + 1;
let nextOrderId = Math.max(...orders.map((o) => o.id)) + 1;

const LATENCY_MS = 350;
const delay = () => new Promise((r) => setTimeout(r, LATENCY_MS));

function notFound(entity: string, id: number): never {
  throw new ApiError("http", `${entity} ${id} no existe (mock)`, 404);
}

export const mockAdapter: DataAdapter = {
  mode: "mock",

  // ── Products ──
  async listProducts() {
    await delay();
    return structuredClone(products);
  },
  async createProduct(input: ProductCreateInput) {
    await delay();
    const product: Product = { id: nextProductId++, ...input };
    products.push(product);
    return structuredClone(product);
  },
  async updateProductPrice(id, price) {
    await delay();
    const p = products.find((x) => x.id === id) ?? notFound("Producto", id);
    p.price = price;
    return structuredClone(p);
  },
  async updateProductUnits(id, units) {
    await delay();
    const p = products.find((x) => x.id === id) ?? notFound("Producto", id);
    p.units = units;
    return structuredClone(p);
  },
  async deleteProduct(id) {
    await delay();
    const i = products.findIndex((x) => x.id === id);
    if (i === -1) notFound("Producto", id);
    products.splice(i, 1);
  },

  // ── Orders ──
  async listOrders() {
    await delay();
    return structuredClone(orders);
  },
  async getOrder(id) {
    await delay();
    return structuredClone(orders.find((o) => o.id === id) ?? notFound("Pedido", id));
  },
  async listOrdersByState(state: OrderState) {
    await delay();
    return structuredClone(orders.filter((o) => o.state === state));
  },
  async listOrdersByUser(userId) {
    await delay();
    return structuredClone(orders.filter((o) => o.user?.id === userId));
  },
  async createOrder(input: OrderCreateInput) {
    await delay();
    const items = input.orderItems.map((it, idx) => {
      const product = products.find((p) => p.id === it.productId);
      if (!product) notFound("Producto", it.productId);
      return { id: idx + 1, product: structuredClone(product), quantity: it.quantity };
    });
    const order: Order = {
      id: nextOrderId++,
      orderDate: new Date().toISOString(),
      state: "IN_CONFIRMATION",
      subTotal: input.subTotal,
      user: null,
      orderItems: items,
    };
    orders.unshift(order);
    return structuredClone(order);
  },
  async updateOrderState(id, state) {
    await delay();
    const o = orders.find((x) => x.id === id) ?? notFound("Pedido", id);
    o.state = state;
    return structuredClone(o);
  },
  async deleteOrder(id) {
    await delay();
    const i = orders.findIndex((x) => x.id === id);
    if (i === -1) notFound("Pedido", id);
    orders.splice(i, 1);
  },

  // ── Users ──
  async listUsers() {
    await delay();
    return structuredClone(users);
  },
  async getUser(id) {
    await delay();
    return structuredClone(users.find((u) => u.id === id) ?? notFound("Cliente", id));
  },
  async createUser(input: UserInput) {
    await delay();
    const user: User = {
      id: nextUserId++,
      name: input.name,
      email: input.email ?? null,
      phoneNumber: input.phoneNumber ?? null,
    };
    users.push(user);
    return structuredClone(user);
  },
  async updateUser(id, input) {
    await delay();
    const u = users.find((x) => x.id === id) ?? notFound("Cliente", id);
    u.name = input.name;
    u.email = input.email ?? u.email;
    u.phoneNumber = input.phoneNumber ?? u.phoneNumber;
    return structuredClone(u);
  },
  async deleteUser(id) {
    await delay();
    const i = users.findIndex((x) => x.id === id);
    if (i === -1) notFound("Cliente", id);
    users.splice(i, 1);
  },

  // ── Health ──
  async checkHealth() {
    return true;
  },
};
