// Datos DEMO/MOCK realistas para DeliverAI (tienda de barrio / SMB).
// Nada de esto es verdad de negocio: solo demo local cuando VITE_MOCK_DATA=true.

import type { Order, Product, User } from "@/lib/types";

export const mockProducts: Product[] = [
  { id: 1, name: "Hamburguesa Clásica", units: 42, price: 12900 },
  { id: 2, name: "Hamburguesa Doble Carne", units: 18, price: 18500 },
  { id: 3, name: "Perro Caliente Especial", units: 35, price: 9800 },
  { id: 4, name: "Salchipapa Familiar", units: 12, price: 22000 },
  { id: 5, name: "Arepa Rellena de Queso", units: 50, price: 7500 },
  { id: 6, name: "Empanadas x6", units: 24, price: 12000 },
  { id: 7, name: "Jugo Natural 16oz", units: 60, price: 6500 },
  { id: 8, name: "Gaseosa 1.5L", units: 8, price: 8000 },
  { id: 9, name: "Limonada de Coco", units: 15, price: 9500 },
  { id: 10, name: "Combo Burger + Papas + Bebida", units: 20, price: 24900 },
];

export const mockUsers: User[] = [
  { id: 1, name: "María Rodríguez", email: "maria.rodriguez@email.com", phoneNumber: 3001234567 },
  { id: 2, name: "Carlos Gómez", email: "carlos.gomez@email.com", phoneNumber: 3109876543 },
  { id: 3, name: "Ana Martínez", email: null, phoneNumber: 3155551234 },
  { id: 4, name: "Luis Torres", email: "luis.torres@email.com", phoneNumber: 3201112233 },
  { id: 5, name: "Paola Sánchez", email: "paola.sanchez@email.com", phoneNumber: 3044445566 },
  { id: 6, name: "Jorge Ramírez", email: null, phoneNumber: 3187778899 },
];

function hoursAgo(h: number): string {
  return new Date(Date.now() - h * 3_600_000).toISOString();
}

let itemId = 1;
function item(productIdx: number, quantity: number) {
  return { id: itemId++, product: mockProducts[productIdx], quantity };
}

export const mockOrders: Order[] = [
  {
    id: 101,
    orderDate: hoursAgo(0.4),
    state: "IN_CONFIRMATION",
    subTotal: 37800,
    user: mockUsers[0],
    orderItems: [item(0, 2), item(6, 2)],
  },
  {
    id: 102,
    orderDate: hoursAgo(1.1),
    state: "IN_CONFIRMATION",
    subTotal: 24900,
    user: mockUsers[2],
    orderItems: [item(9, 1)],
  },
  {
    id: 103,
    orderDate: hoursAgo(1.8),
    state: "PREPARATION",
    subTotal: 44000,
    user: mockUsers[1],
    orderItems: [item(3, 2)],
  },
  {
    id: 104,
    orderDate: hoursAgo(2.5),
    state: "PREPARATION",
    subTotal: 27300,
    user: mockUsers[3],
    orderItems: [item(2, 2), item(6, 1)],
  },
  {
    id: 105,
    orderDate: hoursAgo(3.2),
    state: "PREPARATION",
    subTotal: 18500,
    user: mockUsers[4],
    orderItems: [item(1, 1)],
  },
  {
    id: 106,
    orderDate: hoursAgo(5),
    state: "COMPLETED",
    subTotal: 52400,
    user: mockUsers[1],
    orderItems: [item(9, 2), item(4, 1)],
  },
  {
    id: 107,
    orderDate: hoursAgo(7),
    state: "COMPLETED",
    subTotal: 15000,
    user: mockUsers[5],
    orderItems: [item(4, 2)],
  },
  {
    id: 108,
    orderDate: hoursAgo(9),
    state: "COMPLETED",
    subTotal: 31400,
    user: mockUsers[0],
    orderItems: [item(0, 1), item(2, 1), item(7, 1)],
  },
  {
    id: 109,
    orderDate: hoursAgo(26),
    state: "COMPLETED",
    subTotal: 21500,
    user: mockUsers[3],
    orderItems: [item(8, 1), item(5, 1)],
  },
  {
    id: 110,
    orderDate: hoursAgo(30),
    state: "COMPLETED",
    subTotal: 49800,
    user: mockUsers[4],
    orderItems: [item(9, 2)],
  },
  {
    id: 111,
    orderDate: hoursAgo(50),
    state: "COMPLETED",
    subTotal: 12900,
    user: mockUsers[2],
    orderItems: [item(0, 1)],
  },
  {
    id: 112,
    orderDate: hoursAgo(74),
    state: "COMPLETED",
    subTotal: 36500,
    user: mockUsers[5],
    orderItems: [item(1, 1), item(2, 1), item(7, 1)],
  },
];
