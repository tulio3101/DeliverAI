import { backendAdapter } from "@/lib/api/backend-adapter";
import type { DataAdapter } from "@/lib/api/data-adapter";
import { mockAdapter } from "@/lib/api/mock-adapter";
import { MOCK_DATA } from "@/lib/config";

/**
 * Punto único de acceso a datos para toda la UI.
 * VITE_MOCK_DATA=true  -> mockAdapter (demo local)
 * VITE_MOCK_DATA=false -> backendAdapter (Spring API real)
 */
export const api: DataAdapter = MOCK_DATA ? mockAdapter : backendAdapter;

export type { DataAdapter } from "@/lib/api/data-adapter";
