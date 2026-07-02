import { BrowserRouter, Route, Routes } from "react-router-dom";
import AppLayout from "@/components/layout/app-layout";
import { ThemeProvider } from "@/components/layout/theme-provider";
import CustomersPage from "@/pages/customers";
import DashboardPage from "@/pages/dashboard";
import OrdersPage from "@/pages/orders";
import ProductsPage from "@/pages/products";
import SettingsPage from "@/pages/settings";

export default function App() {
  return (
    <ThemeProvider>
      <BrowserRouter>
        <Routes>
          <Route element={<AppLayout />}>
            <Route index element={<DashboardPage />} />
            <Route path="pedidos" element={<OrdersPage />} />
            <Route path="productos" element={<ProductsPage />} />
            <Route path="clientes" element={<CustomersPage />} />
            <Route path="ajustes" element={<SettingsPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
}
