// Config central de entorno.
// Vite solo expone al cliente variables VITE_*; por eso la app lee
// VITE_MOCK_DATA. MOCK_DATA (sin prefijo) se mantiene en .env solo como
// alias operativo/documental. Ver README.

export const API_BASE_URL: string = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export const MOCK_DATA: boolean =
  String(import.meta.env.VITE_MOCK_DATA ?? "true").toLowerCase() !== "false";

export const REQUEST_TIMEOUT_MS = 6000;
