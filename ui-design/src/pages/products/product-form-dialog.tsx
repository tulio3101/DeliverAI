import { type FormEvent, useEffect, useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
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

interface ProductFormDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSaved: () => void;
}

interface FormErrors {
  name?: string;
  price?: string;
  units?: string;
}

function validate(name: string, price: string, units: string): FormErrors {
  const errors: FormErrors = {};
  if (!name.trim()) errors.name = "El nombre es obligatorio.";
  if (!price.trim() || Number(price) <= 0) errors.price = "El precio debe ser mayor que 0.";
  if (!units.trim() || Number(units) < 0) errors.units = "Las unidades deben ser 0 o más.";
  return errors;
}

export function ProductFormDialog({ open, onOpenChange, onSaved }: ProductFormDialogProps) {
  const [name, setName] = useState("");
  const [price, setPrice] = useState("");
  const [units, setUnits] = useState("");
  const [errors, setErrors] = useState<FormErrors>({});
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!open) return;
    setName("");
    setPrice("");
    setUnits("");
    setErrors({});
  }, [open]);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    const nextErrors = validate(name, price, units);
    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) return;

    setSubmitting(true);
    try {
      await api.createProduct({
        name: name.trim(),
        price: Number(price),
        units: Number(units),
      });
      toast.success(`Producto "${name.trim()}" creado`);
      onSaved();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "No se pudo crear el producto.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Dialog
      open={open}
      onOpenChange={(next) => {
        if (!submitting) onOpenChange(next);
      }}
    >
      <DialogContent>
        <form onSubmit={handleSubmit} className="flex flex-col gap-4" noValidate>
          <DialogHeader>
            <DialogTitle>Nuevo producto</DialogTitle>
            <DialogDescription>Registra un producto para el catálogo operativo.</DialogDescription>
          </DialogHeader>

          <div className="flex flex-col gap-1.5">
            <Label htmlFor="product-name">Nombre</Label>
            <Input
              id="product-name"
              value={name}
              onChange={(event) => setName(event.target.value)}
              aria-invalid={errors.name ? true : undefined}
              disabled={submitting}
              required
            />
            {errors.name && <p className="text-[0.7rem] text-destructive">{errors.name}</p>}
          </div>

          <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="product-price">Precio</Label>
              <Input
                id="product-price"
                type="number"
                min="1"
                inputMode="numeric"
                value={price}
                onChange={(event) => setPrice(event.target.value)}
                aria-invalid={errors.price ? true : undefined}
                disabled={submitting}
                required
              />
              {errors.price && <p className="text-[0.7rem] text-destructive">{errors.price}</p>}
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="product-units">Unidades</Label>
              <Input
                id="product-units"
                type="number"
                min="0"
                inputMode="numeric"
                value={units}
                onChange={(event) => setUnits(event.target.value)}
                aria-invalid={errors.units ? true : undefined}
                disabled={submitting}
                required
              />
              {errors.units && <p className="text-[0.7rem] text-destructive">{errors.units}</p>}
            </div>
          </div>

          <DialogFooter>
            <Button
              variant="outline"
              type="button"
              onClick={() => onOpenChange(false)}
              disabled={submitting}
            >
              Cancelar
            </Button>
            <Button type="submit" disabled={submitting}>
              {submitting ? "Creando…" : "Crear producto"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
