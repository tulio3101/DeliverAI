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
import type { User } from "@/lib/types";

interface CustomerFormDialogProps {
  /** Controla si el diálogo está abierto. */
  open: boolean;
  onOpenChange: (open: boolean) => void;
  /** Cliente a editar. `null` = modo creación. */
  user: User | null;
  /** Se invoca tras guardar exitosamente (crear o editar). */
  onSaved: () => void;
}

interface FormErrors {
  name?: string;
  email?: string;
  phoneNumber?: string;
}

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PHONE_RE = /^\d+$/;

function validate(name: string, email: string, phoneNumber: string): FormErrors {
  const errors: FormErrors = {};
  if (!name.trim()) errors.name = "El nombre es obligatorio.";
  if (email.trim() && !EMAIL_RE.test(email.trim())) {
    errors.email = "Ingresa un email válido.";
  }
  if (phoneNumber.trim() && !PHONE_RE.test(phoneNumber.trim())) {
    errors.phoneNumber = "El teléfono debe contener solo números.";
  }
  return errors;
}

/**
 * Diálogo único para crear o editar un cliente (según se pase `user`).
 */
export function CustomerFormDialog({ open, onOpenChange, user, onSaved }: CustomerFormDialogProps) {
  const isEdit = user !== null;

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [errors, setErrors] = useState<FormErrors>({});
  const [submitting, setSubmitting] = useState(false);

  // Prefill / reset cada vez que el diálogo se abre.
  useEffect(() => {
    if (!open) return;
    setName(user?.name ?? "");
    setEmail(user?.email ?? "");
    setPhoneNumber(user?.phoneNumber != null ? String(user.phoneNumber) : "");
    setErrors({});
  }, [open, user]);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    const nextErrors = validate(name, email, phoneNumber);
    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) return;

    const trimmedName = name.trim();
    const input = {
      name: trimmedName,
      email: email.trim() ? email.trim() : undefined,
      phoneNumber: phoneNumber.trim() ? Number(phoneNumber.trim()) : undefined,
    };

    setSubmitting(true);
    try {
      if (isEdit && user) {
        await api.updateUser(user.id, input);
        toast.success(`Cliente "${trimmedName}" actualizado`);
      } else {
        await api.createUser(input);
        toast.success(`Cliente "${trimmedName}" creado`);
      }
      onSaved();
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "No se pudo guardar el cliente. Intenta de nuevo.";
      toast.error(message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Dialog
      open={open}
      onOpenChange={(next) => {
        if (submitting) return;
        onOpenChange(next);
      }}
    >
      <DialogContent>
        <form onSubmit={handleSubmit} className="flex flex-col gap-4" noValidate>
          <DialogHeader>
            <DialogTitle>{isEdit ? "Editar cliente" : "Nuevo cliente"}</DialogTitle>
            <DialogDescription>
              {isEdit
                ? "Actualiza los datos del cliente."
                : "Completa los datos para registrar un nuevo cliente."}
            </DialogDescription>
          </DialogHeader>

          <div className="flex flex-col gap-1.5">
            <Label htmlFor="customer-name">Nombre</Label>
            <Input
              id="customer-name"
              value={name}
              onChange={(event) => setName(event.target.value)}
              placeholder="Nombre completo"
              aria-invalid={errors.name ? true : undefined}
              aria-describedby={errors.name ? "customer-name-error" : undefined}
              disabled={submitting}
              autoComplete="name"
              required
            />
            {errors.name && (
              <p id="customer-name-error" className="text-[0.7rem] text-destructive">
                {errors.name}
              </p>
            )}
          </div>

          <div className="flex flex-col gap-1.5">
            <Label htmlFor="customer-email">Email (opcional)</Label>
            <Input
              id="customer-email"
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="correo@ejemplo.com"
              aria-invalid={errors.email ? true : undefined}
              aria-describedby={errors.email ? "customer-email-error" : undefined}
              disabled={submitting}
              autoComplete="email"
            />
            {errors.email && (
              <p id="customer-email-error" className="text-[0.7rem] text-destructive">
                {errors.email}
              </p>
            )}
          </div>

          <div className="flex flex-col gap-1.5">
            <Label htmlFor="customer-phone">Teléfono (opcional)</Label>
            <Input
              id="customer-phone"
              type="tel"
              inputMode="numeric"
              value={phoneNumber}
              onChange={(event) => setPhoneNumber(event.target.value)}
              placeholder="3001234567"
              aria-invalid={errors.phoneNumber ? true : undefined}
              aria-describedby={errors.phoneNumber ? "customer-phone-error" : undefined}
              disabled={submitting}
              autoComplete="tel"
            />
            {errors.phoneNumber && (
              <p id="customer-phone-error" className="text-[0.7rem] text-destructive">
                {errors.phoneNumber}
              </p>
            )}
          </div>

          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={submitting}
            >
              Cancelar
            </Button>
            <Button type="submit" disabled={submitting}>
              {submitting ? "Guardando…" : isEdit ? "Guardar cambios" : "Crear cliente"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
