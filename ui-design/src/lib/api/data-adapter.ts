import type {
  DataMode,
  Order,
  OrderCreateInput,
  OrderState,
  Product,
  ProductCreateInput,
  User,
  UserInput,
} from "@/lib/types";

/**
 * Contrato único de acceso a datos. La UI solo consume este interface
 * (vía src/lib/api/index.ts); nunca fetch directo en componentes.
 */
export interface DataAdapter {
  mode: DataMode;

  // Products
  listProducts(): Promise<Product[]>;
  createProduct(input: ProductCreateInput): Promise<Product>;
  updateProductPrice(id: number, price: number): Promise<Product>;
  updateProductUnits(id: number, units: number): Promise<Product>;
  deleteProduct(id: number): Promise<void>;

  // Orders
  listOrders(): Promise<Order[]>;
  getOrder(id: number): Promise<Order>;
  listOrdersByState(state: OrderState): Promise<Order[]>;
  listOrdersByUser(userId: number): Promise<Order[]>;
  createOrder(input: OrderCreateInput): Promise<Order>;
  updateOrderState(id: number, state: OrderState): Promise<Order>;
  deleteOrder(id: number): Promise<void>;

  // Users
  listUsers(): Promise<User[]>;
  getUser(id: number): Promise<User>;
  createUser(input: UserInput): Promise<User>;
  updateUser(id: number, input: UserInput): Promise<User>;
  deleteUser(id: number): Promise<void>;

  // Health
  checkHealth(): Promise<boolean>;
}
