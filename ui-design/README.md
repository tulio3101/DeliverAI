# DeliverAI Admin UI

Admin UI operacional (MVP) para DeliverAI: revisión de pedidos, estados, clientes y catálogo de productos. Construida con React + Vite + TypeScript + Tailwind CSS v4 + shadcn/ui + Biome. Estética liquid/glass tipo macOS/iOS.

## Requisitos

- Node.js 20+
- pnpm recomendado. Si no está activo, ejecutar:

```bash
corepack enable
corepack prepare pnpm@latest --activate
```

- Backend opcional: Spring Boot en `http://localhost:8080` (ver raíz del repo, `docker-compose.yml`).

## Arranque con pnpm

Trabajar siempre desde el root del frontend:

```bash
cd /home/onecode/ECI/DeliverAI/ui-design
```

Instalar dependencias:

```bash
pnpm install
```

Levantar dev server:

```bash
pnpm dev
```

Si el puerto `5173` está ocupado:

```bash
pnpm dev -- --host 127.0.0.1 --port 5174
```

## Linting, formato y build

Biome check completo:

```bash
pnpm check
```

Biome lint:

```bash
pnpm lint
```

Biome auto-fix:

```bash
pnpm check:fix
```

Formato:

```bash
pnpm format
```

TypeScript y build:

```bash
pnpm type-check
pnpm build
```

Servir build:

```bash
pnpm preview
```

## Variables de entorno

Copiar `.env.example` a `.env`:

| Variable | Default | Uso |
|---|---|---|
| `VITE_MOCK_DATA` | `true` | `true`: datos demo locales. `false`: API real. |
| `VITE_API_BASE_URL` | `http://localhost:8080` | Base URL del backend Spring. |
| `MOCK_DATA` | `true` | **Alias operativo/documental** del mismo concepto. |

> **Nota `MOCK_DATA` vs `VITE_MOCK_DATA`:** Vite solo expone al cliente variables con prefijo `VITE_`. Por eso el código lee **`VITE_MOCK_DATA`**. `MOCK_DATA` se mantiene en `.env` porque fue pedido explícitamente como flag operativo, pero no es visible para la app en el navegador. Cambios en `.env` requieren reiniciar el dev server.

## Modos de datos

- **`VITE_MOCK_DATA=true` (demo):** adapter mock en memoria con datos realistas etiquetados como demo en la UI (badge/banner "Datos demo").
- **`VITE_MOCK_DATA=false` (API real):** adapter contra el backend Spring. Detecta backend caído/timeout/errores HTTP y muestra banner de degradación + toasts sin romper la UI. Health check: `GET /v3/api-docs` cada 15s.

## Endpoints backend usados

Según `Agents/project/api-inventory.md`:

- Products: `POST /products`, `PATCH /products/{id}/price`, `PATCH /products/{id}/units`, `DELETE /products/{id}`
- Orders: `POST /order`, `PATCH /order/{id}?state=`, `DELETE /order/{id}`, `GET /order/{id}`, `GET /order/user/{userId}`, `GET /order/state?state=`
- Users: `POST /user`, `GET /user/{id}`, `GET /user/all`, `PUT /user/{id}`, `DELETE /user/{id}`
- Health: `GET /v3/api-docs`

## Gaps conocidos del backend (reflejados en UI)

- **No existe `GET /products` (listado).** En modo API real, la vista Productos muestra el gap explícitamente; las mutaciones (crear/actualizar/eliminar) sí operan contra la API.
- El listado global de pedidos se compone con 3 llamadas a `GET /order/state` (no hay `GET /order` global).
- `OrderRequestDTO` no expone `userId`: los pedidos creados desde la UI quedan sin usuario asociado (limitación del backend).
- n8n/WhatsApp no implementado (fuera del alcance del frontend).

## Arquitectura frontend

```
src/
  lib/
    config.ts          # env central (VITE_*)
    types.ts           # tipos alineados a DTOs del backend
    format.ts          # formateadores es-CO
    mock-data.ts       # datos demo
    api/
      data-adapter.ts  # contrato DataAdapter (única interfaz de datos de la UI)
      http.ts          # fetch con timeout + ApiError tipado
      backend-adapter.ts
      mock-adapter.ts
      index.ts         # selección de adapter por VITE_MOCK_DATA
  hooks/               # useAsyncData, useBackendStatus
  components/
    ui/                # shadcn/ui (generado)
    layout/            # shell: sidebar, header, banners, tema
  pages/               # dashboard, orders, products, customers, settings
```

Regla: los componentes de página nunca hacen `fetch` directo; todo pasa por `api` (`src/lib/api`).
