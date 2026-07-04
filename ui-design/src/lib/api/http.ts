import { API_BASE_URL, REQUEST_TIMEOUT_MS } from "@/lib/config";
import { ApiError } from "@/lib/types";

interface HttpOptions {
  method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  body?: unknown;
  /** Query params; se omiten valores undefined. */
  params?: Record<string, string | number | undefined>;
}

/**
 * Cliente HTTP mínimo con timeout y errores tipados (ApiError).
 * Toda llamada al backend real pasa por aquí.
 */
export async function http<T>(path: string, opts: HttpOptions = {}): Promise<T> {
  const url = new URL(path, API_BASE_URL);
  if (opts.params) {
    for (const [k, v] of Object.entries(opts.params)) {
      if (v !== undefined) url.searchParams.set(k, String(v));
    }
  }

  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS);

  let res: Response;
  try {
    res = await fetch(url, {
      method: opts.method ?? "GET",
      headers: opts.body !== undefined ? { "Content-Type": "application/json" } : undefined,
      body: opts.body !== undefined ? JSON.stringify(opts.body) : undefined,
      signal: controller.signal,
    });
  } catch (err) {
    clearTimeout(timer);
    if (err instanceof DOMException && err.name === "AbortError") {
      throw new ApiError("timeout", `Timeout tras ${REQUEST_TIMEOUT_MS}ms: ${path}`);
    }
    throw new ApiError("network", `No se pudo conectar al backend (${API_BASE_URL})`);
  }
  clearTimeout(timer);

  if (!res.ok) {
    let detail = "";
    try {
      const data = await res.json();
      detail = typeof data?.message === "string" ? data.message : JSON.stringify(data);
    } catch {
      // cuerpo no-JSON: se ignora
    }
    throw new ApiError("http", detail || `Error HTTP ${res.status} en ${path}`, res.status);
  }

  if (res.status === 204) return undefined as T;
  const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
}
