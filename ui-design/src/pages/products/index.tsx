import { Construction, Package, Plus, RefreshCw, Search } from "lucide-react";
import { useMemo, useState } from "react";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { useAsyncData } from "@/hooks/use-async-data";
import { api } from "@/lib/api";
import { MOCK_DATA } from "@/lib/config";
import type { Product } from "@/lib/types";
import { ProductCard } from "@/pages/products/product-card";
import { ProductFormDialog } from "@/pages/products/product-form-dialog";

type SortKey = "name" | "price" | "stock";

const SKELETON_CARDS = ["a", "b", "c", "d", "e", "f"];

function sortProducts(products: Product[], sort: SortKey): Product[] {
  return [...products].sort((a, b) => {
    if (sort === "name") return a.name.localeCompare(b.name, "es");
    if (sort === "price") return b.price - a.price;
    return a.units - b.units;
  });
}

export default function ProductsPage() {
  const { data, loading, error, reload } = useAsyncData(() => api.listProducts(), []);
  const [search, setSearch] = useState("");
  const [sort, setSort] = useState<SortKey>("name");
  const [formOpen, setFormOpen] = useState(false);

  const products = useMemo(() => {
    const query = search.trim().toLowerCase();
    const base = data ?? [];
    return sortProducts(
      query ? base.filter((product) => product.name.toLowerCase().includes(query)) : base,
      sort,
    );
  }, [data, search, sort]);

  return (
    <>
      <div className="mx-auto flex w-full max-w-7xl flex-col gap-4 p-4 sm:p-6">
        <header className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
          <div className="min-w-0">
            <div className="flex flex-wrap items-center gap-2">
              <h2 className="font-heading text-xl font-semibold">Productos</h2>
              {MOCK_DATA && <Badge variant="secondary">Datos demo</Badge>}
            </div>
            <p className="text-muted-foreground">Catálogo, precio, stock y creación de producto.</p>
          </div>
          <Button onClick={() => setFormOpen(true)}>
            <Plus aria-hidden="true" /> Nuevo producto
          </Button>
        </header>

        <Card className="glass rounded-2xl">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Package className="size-4" aria-hidden="true" /> Catálogo
            </CardTitle>
            <CardDescription>
              El listado usa mock cuando el backend real no expone GET /products.
            </CardDescription>
          </CardHeader>
          <CardContent className="flex flex-col gap-4">
            <div className="flex flex-col gap-3 sm:flex-row">
              <div className="relative flex-1">
                <Search
                  className="pointer-events-none absolute top-1/2 left-2 size-3.5 -translate-y-1/2 text-muted-foreground"
                  aria-hidden="true"
                />
                <Input
                  value={search}
                  onChange={(event) => setSearch(event.target.value)}
                  className="pl-7"
                  placeholder="Buscar producto…"
                  aria-label="Buscar productos"
                  disabled={loading || !!error}
                />
              </div>
              <Select value={sort} onValueChange={(value) => setSort(value as SortKey)}>
                <SelectTrigger className="w-full sm:w-44" aria-label="Ordenar productos">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="name">Nombre</SelectItem>
                  <SelectItem value="price">Precio</SelectItem>
                  <SelectItem value="stock">Stock bajo</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {loading && (
              <div className="grid grid-cols-1 gap-3 md:grid-cols-2 xl:grid-cols-3">
                {SKELETON_CARDS.map((key) => (
                  <Skeleton key={key} className="h-44 rounded-2xl" />
                ))}
              </div>
            )}

            {!loading && error?.kind === "unsupported" && (
              <Alert className="border-amber-500/30 bg-amber-500/10 text-amber-900 dark:text-amber-200">
                <Construction aria-hidden="true" />
                <AlertTitle>Listado de productos no disponible en API real</AlertTitle>
                <AlertDescription>
                  El backend actual no expone un endpoint de listado de productos (GAP documentado).
                  Las operaciones de crear, actualizar y eliminar sí funcionan contra la API real,
                  pero el catálogo no puede mostrarse.
                </AlertDescription>
              </Alert>
            )}

            {!loading && error && error.kind !== "unsupported" && (
              <Alert variant="destructive">
                <Construction aria-hidden="true" />
                <AlertTitle>No se pudieron cargar los productos</AlertTitle>
                <AlertDescription>
                  <p>{error.message}</p>
                  <Button size="sm" variant="outline" className="mt-2" onClick={reload}>
                    <RefreshCw aria-hidden="true" /> Reintentar
                  </Button>
                </AlertDescription>
              </Alert>
            )}

            {!loading && !error && products.length === 0 && (
              <div className="flex flex-col items-center gap-2 py-10 text-center text-muted-foreground">
                <Package className="size-8" aria-hidden="true" />
                <p>No hay productos para este filtro.</p>
              </div>
            )}

            {!loading && !error && products.length > 0 && (
              <div className="grid grid-cols-1 gap-3 md:grid-cols-2 xl:grid-cols-3">
                {products.map((product) => (
                  <ProductCard key={product.id} product={product} onChanged={reload} />
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      </div>

      <ProductFormDialog
        open={formOpen}
        onOpenChange={setFormOpen}
        onSaved={() => {
          setFormOpen(false);
          reload();
        }}
      />
    </>
  );
}
