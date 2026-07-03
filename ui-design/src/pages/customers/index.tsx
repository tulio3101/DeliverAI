import { RefreshCw, Search, TriangleAlert, Users } from "lucide-react";
import { useMemo, useState } from "react";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { useAsyncData } from "@/hooks/use-async-data";
import { api } from "@/lib/api";
import { MOCK_DATA } from "@/lib/config";
import type { ApiErrorKind, User } from "@/lib/types";

const ERROR_TITLES: Record<ApiErrorKind, string> = {
  network: "No se pudo conectar con el backend",
  timeout: "Tiempo de espera agotado",
  http: "Error del servidor",
  unsupported: "Función no disponible",
};

const SKELETON_ROWS = ["a", "b", "c", "d", "e"];

function formatPhone(phoneNumber: number | null): string {
  return phoneNumber === null ? "—" : String(phoneNumber);
}

function matchesQuery(user: User, query: string): boolean {
  const haystacks = [
    user.name,
    user.email ?? "",
    user.phoneNumber !== null ? String(user.phoneNumber) : "",
  ];
  return haystacks.some((field) => field.toLowerCase().includes(query));
}

export default function CustomersPage() {
  // Clientes son solo lectura en el dashboard: los crea el agente al registrar
  // el pedido (findOrCreateByPhoneNumber). El backend soporta escritura, pero
  // la UI no la expone por decisión de diseño.
  const { data: users, loading, error, reload } = useAsyncData(() => api.listUsers(), []);
  const [search, setSearch] = useState("");

  const filtered = useMemo(() => {
    if (!users) return [];
    const query = search.trim().toLowerCase();
    if (!query) return users;
    return users.filter((user) => matchesQuery(user, query));
  }, [users, search]);

  return (
    <div className="mx-auto flex w-full max-w-5xl flex-col gap-4 p-4 sm:p-6">
      <header className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex items-center gap-2">
          <div>
            <h1 className="font-heading text-lg font-semibold">Clientes</h1>
            <p className="text-xs text-muted-foreground">Clientes registrados por el agente.</p>
          </div>
          {MOCK_DATA && <Badge variant="secondary">Datos demo</Badge>}
        </div>
      </header>

      <Card className="glass rounded-2xl">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Users className="size-4" aria-hidden="true" /> Listado de clientes
          </CardTitle>
          <CardDescription>
            {users
              ? `${users.length} cliente${users.length === 1 ? "" : "s"} registrado${users.length === 1 ? "" : "s"}`
              : "Cargando clientes…"}
          </CardDescription>
        </CardHeader>
        <CardContent className="flex flex-col gap-4">
          <div className="relative">
            <Search
              className="pointer-events-none absolute top-1/2 left-2 size-3.5 -translate-y-1/2 text-muted-foreground"
              aria-hidden="true"
            />
            <Input
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              placeholder="Buscar por nombre, email o teléfono…"
              className="pl-7"
              aria-label="Buscar clientes"
              disabled={loading || !!error}
            />
          </div>

          {loading && (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Nombre</TableHead>
                  <TableHead>Email</TableHead>
                  <TableHead>Teléfono</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {SKELETON_ROWS.map((key) => (
                  <TableRow key={key}>
                    <TableCell colSpan={4}>
                      <Skeleton className="h-5 w-full" />
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}

          {!loading && error && (
            <Alert variant="destructive">
              <TriangleAlert aria-hidden="true" />
              <AlertTitle>{ERROR_TITLES[error.kind]}</AlertTitle>
              <AlertDescription>
                <p>{error.message}</p>
                <div className="mt-2">
                  <Button size="sm" variant="outline" onClick={reload}>
                    <RefreshCw aria-hidden="true" /> Reintentar
                  </Button>
                </div>
              </AlertDescription>
            </Alert>
          )}

          {!loading && !error && users && users.length === 0 && (
            <div className="flex flex-col items-center gap-2 py-10 text-center text-muted-foreground">
              <Users className="size-8" aria-hidden="true" />
              <p className="text-sm">Aún no hay clientes registrados.</p>
            </div>
          )}

          {!loading && !error && users && users.length > 0 && filtered.length === 0 && (
            <div className="flex flex-col items-center gap-2 py-10 text-center text-muted-foreground">
              <Search className="size-8" aria-hidden="true" />
              <p className="text-sm">Sin resultados para &quot;{search}&quot;.</p>
            </div>
          )}

          {!loading && !error && filtered.length > 0 && (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Nombre</TableHead>
                  <TableHead>Email</TableHead>
                  <TableHead>Teléfono</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filtered.map((user) => (
                  <TableRow key={user.id}>
                    <TableCell className="text-muted-foreground">{user.id}</TableCell>
                    <TableCell className="font-medium">{user.name}</TableCell>
                    <TableCell>{user.email ?? "—"}</TableCell>
                    <TableCell>{formatPhone(user.phoneNumber)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
