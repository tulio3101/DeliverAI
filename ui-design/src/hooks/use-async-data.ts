import { useCallback, useEffect, useRef, useState } from "react";
import { ApiError } from "@/lib/types";

export interface AsyncState<T> {
  data: T | null;
  loading: boolean;
  error: ApiError | null;
  /** Vuelve a ejecutar el fetch. */
  reload: () => void;
}

/**
 * Hook estándar de carga de datos para toda la app.
 * Garantiza estados loading/error/data consistentes y evita
 * setState tras unmount.
 */
export function useAsyncData<T>(fetcher: () => Promise<T>, deps: unknown[] = []): AsyncState<T> {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<ApiError | null>(null);
  const [tick, setTick] = useState(0);
  const alive = useRef(true);

  useEffect(() => {
    alive.current = true;
    return () => {
      alive.current = false;
    };
  }, []);

  // biome-ignore lint/correctness/useExhaustiveDependencies: deps es la lista externa del caller
  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);
    fetcher()
      .then((d) => {
        if (!cancelled && alive.current) {
          setData(d);
          setLoading(false);
        }
      })
      .catch((e) => {
        if (!cancelled && alive.current) {
          setError(e instanceof ApiError ? e : new ApiError("network", String(e)));
          setLoading(false);
        }
      });
    return () => {
      cancelled = true;
    };
  }, [...deps, tick]);

  const reload = useCallback(() => setTick((t) => t + 1), []);

  return { data, loading, error, reload };
}
