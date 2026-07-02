import { Outlet } from "react-router-dom";
import { BackendBanner } from "@/components/layout/backend-banner";
import { Header } from "@/components/layout/header";
import { Sidebar } from "@/components/layout/sidebar";
import { Toaster } from "@/components/ui/sonner";
import { useBackendStatus } from "@/hooks/use-backend-status";

export default function AppLayout() {
  const backendStatus = useBackendStatus();

  return (
    <div className="app-bg min-h-dvh text-foreground">
      <div className="flex min-h-dvh">
        <Sidebar />
        <div className="flex min-w-0 flex-1 flex-col">
          <Header status={backendStatus} />
          <BackendBanner status={backendStatus} />
          <main className="flex-1">
            <Outlet />
          </main>
        </div>
      </div>
      <Toaster richColors position="top-right" />
    </div>
  );
}
