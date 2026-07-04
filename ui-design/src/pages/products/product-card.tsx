import { Pencil, Trash2, TriangleAlert } from "lucide-react";
import { type FormEvent, useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { api } from "@/lib/api";
import { formatMoney } from "@/lib/format";
import type { Product } from "@/lib/types";

interface ProductCardProps {
  product: Product;
  onChanged: () => void;
}

type EditMode = "price" | "units";

export function ProductCard({ product, onChanged }: ProductCardProps) {
  const [editMode, setEditMode] = useState<EditMode | null>(null);
  const [value, setValue] = useState("");
  const [saving, setSaving] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);

  function openEdit(mode: EditMode) {
    setEditMode(mode);
    setValue(String(mode === "price" ? product.price : product.units));
  }

  async function handleEditSubmit(event: FormEvent) {
    event.preventDefault();
    if (!editMode) return;
    const numericValue = Number(value);
    if (editMode === "price" && numericValue <= 0) {
      toast.error("El precio debe ser mayor que 0.");
      return;
    }
    if (editMode === "units" && numericValue < 0) {
      toast.error("Las unidades deben ser 0 o más.");
      return;
    }

    setSaving(true);
    try {
      if (editMode === "price") await api.updateProductPrice(product.id, numericValue);
      if (editMode === "units") await api.updateProductUnits(product.id, numericValue);
      toast.success(`Producto "${product.name}" actualizado`);
      setEditMode(null);
      onChanged();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "No se pudo actualizar el producto.");
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete() {
    setSaving(true);
    try {
      await api.deleteProduct(product.id);
      toast.success(`Producto "${product.name}" eliminado`);
      setDeleteOpen(false);
      onChanged();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "No se pudo eliminar el producto.");
    } finally {
      setSaving(false);
    }
  }

  const stockLow = product.units < 10;

  return (
    <>
      <Card className="glass rounded-2xl transition-transform hover:-translate-y-0.5">
        <CardHeader>
          <div className="flex items-start justify-between gap-3">
            <CardTitle className="min-w-0 truncate">{product.name}</CardTitle>
            <Badge
              variant="outline"
              className={
                stockLow
                  ? "border-red-500/30 bg-red-500/15 text-red-700 dark:text-red-300"
                  : "bg-background/40"
              }
            >
              {stockLow && <TriangleAlert className="size-3" aria-hidden="true" />}
              {product.units} unidades
            </Badge>
          </div>
        </CardHeader>
        <CardContent className="flex flex-col gap-4">
          <p className="font-heading text-2xl font-semibold tabular-nums">
            {formatMoney(product.price)}
          </p>
          <div className="grid grid-cols-3 gap-2">
            <Button variant="outline" onClick={() => openEdit("price")}>
              <Pencil aria-hidden="true" /> Precio
            </Button>
            <Button variant="outline" onClick={() => openEdit("units")}>
              <Pencil aria-hidden="true" /> Stock
            </Button>
            <Button variant="destructive" onClick={() => setDeleteOpen(true)}>
              <Trash2 aria-hidden="true" /> Eliminar
            </Button>
          </div>
        </CardContent>
      </Card>

      <Dialog
        open={editMode !== null}
        onOpenChange={(next) => !saving && !next && setEditMode(null)}
      >
        <DialogContent>
          <form onSubmit={handleEditSubmit} className="flex flex-col gap-4">
            <DialogHeader>
              <DialogTitle>
                {editMode === "price" ? "Editar precio" : "Editar unidades"}
              </DialogTitle>
              <DialogDescription>{product.name}</DialogDescription>
            </DialogHeader>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor={`product-${product.id}-${editMode ?? "value"}`}>
                {editMode === "price" ? "Precio" : "Unidades"}
              </Label>
              <Input
                id={`product-${product.id}-${editMode ?? "value"}`}
                type="number"
                min={editMode === "price" ? "1" : "0"}
                inputMode="numeric"
                value={value}
                onChange={(event) => setValue(event.target.value)}
                disabled={saving}
                required
              />
            </div>
            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => setEditMode(null)}
                disabled={saving}
              >
                Cancelar
              </Button>
              <Button type="submit" disabled={saving}>
                {saving ? "Guardando…" : "Guardar"}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      <Dialog open={deleteOpen} onOpenChange={(next) => !saving && setDeleteOpen(next)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Eliminar producto</DialogTitle>
            <DialogDescription>
              ¿Seguro que deseas eliminar &quot;{product.name}&quot;? Esta acción no se puede
              deshacer.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDeleteOpen(false)} disabled={saving}>
              Cancelar
            </Button>
            <Button variant="destructive" onClick={handleDelete} disabled={saving}>
              {saving ? "Eliminando…" : "Eliminar"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}
