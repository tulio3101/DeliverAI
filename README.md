<div align="center">

# 🚀 DeliverAI

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4-brightgreen?logo=spring)
![Maven](https://img.shields.io/badge/Maven-3.9-blue?logo=apachemaven)
![Spring Data JPA](https://img.shields.io/badge/JPA-Hibernate-6DB33F?logo=hibernate)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3.0-green?logo=swagger)
![React](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-8-646CFF?logo=vite&logoColor=white)
![Tailwind](https://img.shields.io/badge/Tailwind_CSS-4-38B2AC?logo=tailwindcss&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-S3_+_CloudFront-FF9900?logo=amazonwebservices&logoColor=white)
![Terraform](https://img.shields.io/badge/Terraform-IaC-7B42BC?logo=terraform&logoColor=white)
![n8n](https://img.shields.io/badge/n8n-Workflow_Automation-EA4AAA?logo=n8n&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow)

</div>

**DeliverAI** is an academic project developed for the course **Hyperautomation Architectures: Design, Implementation and Governance of AI Agents in Business Contexts** at the **Colombian School of Engineering Julio Garavito** (intersemester period 2026-I).

It automates the WhatsApp ordering flow for small and medium businesses: the customer texts the business, an AI agent orchestrated in **n8n** understands the order, confirms it and persists it through a **REST API**; the business owner manages it from the **Admin UI**.

<div align="center">

![DeliverAI system architecture](media/architecture-diagram.png)

</div>

---

## 📌 Current state vs target

| Component | Status | Where it runs |
|---|---|---|
| **REST API** (Spring Boot) | ✅ Implemented in `src/` | Deployed **externally** on Azure Web App |
| **PostgreSQL** | ✅ Operational | Next to the backend (Docker Compose locally) |
| **Admin UI** (React) | ✅ Implemented in `ui-design/` | Deployed on **AWS S3 + CloudFront** |
| **Frontend infra** (Terraform) | ✅ Implemented in `infra/aws/frontend/` | Applied in AWS `us-east-1` |
| **Frontend CI/CD** | ✅ GitHub Actions + OIDC | GitHub |
| **n8n workflow + AI agent** | ⚠️ **External** — runs outside the repo; JSON export pending in `n8n/` | External n8n instance |
| **WhatsApp Business** | ⚠️ External/pending documentation in repo | Meta / external n8n |
| **Push notifications (SSE/WS)** | ❌ Pending — MVP uses REST polling | — |

> The frontend supports **mock mode** (`VITE_MOCK_DATA=true`, demo data without a backend) and **real API mode**.

---

## 🏗️ System architecture (System Context)

```mermaid
flowchart LR
    C["Customer<br/>WhatsApp"] -->|messages| WA["WhatsApp Business"]
    OP["Operator /<br/>business owner"] -->|HTTPS| CF["CloudFront + S3<br/>AWS us-east-1"]

    subgraph EXT["External services"]
        WA --> N8N["n8n workflow +<br/>AI agent - external"]
        N8N -.->|inference| LLM["LLM provider"]
    end

    subgraph AZ["Azure"]
        API["DeliverAI API<br/>Spring Boot 3.4 / Java 21"]
        DB[("PostgreSQL")]
        API --> DB
    end

    N8N -->|"HTTP REST"| API
    CF -->|"static SPA"| OP
    CF -->|"proxy /order* /products* /user* /v3/*"| API
```

The **Admin UI** calls the API through CloudFront (same origin → no CORS or mixed content). The conversational flow (WhatsApp → n8n → LLM) runs **outside this repo**; its JSON export is pending in `n8n/`.

### Containers (C4 Container)

```mermaid
flowchart TB
    subgraph AWS["AWS us-east-1"]
        S3["Private S3 bucket<br/>static assets"]
        CFD["CloudFront distribution<br/>HTTPS + CDN + SPA fallback"]
        CFD -->|"OAC SigV4"| S3
    end

    subgraph Azure["Azure Web App"]
        SPRING["Spring Boot API<br/>Controller-Service-Repository"]
        PG[("PostgreSQL 16")]
        SPRING --> PG
    end

    subgraph External["External n8n platform"]
        WF["n8n workflow"]
        AG["Conversational agent"]
        WF --> AG
    end

    BROWSER["Operator's browser<br/>React SPA"] -->|HTTPS| CFD
    CFD -->|"API behaviors<br/>cache disabled"| SPRING
    BROWSER -.->|"polling every 10s<br/>GET /order/state"| CFD
    AG -.->|LLM| PROV["Model provider"]
    WF -->|"POST /order, /user"| SPRING
```

---

## 🔄 End-to-end order flow

```mermaid
sequenceDiagram
    actor Customer
    participant WA as WhatsApp Business
    participant N8N as n8n + AI agent (external)
    participant API as DeliverAI API (Azure)
    participant DB as PostgreSQL
    participant UI as Admin UI (CloudFront)
    actor Operator

    Customer->>WA: "I want 2 chocolate cakes"
    WA->>N8N: inbound message webhook
    N8N->>N8N: agent interprets intent (LLM)
    N8N->>Customer: clarifying questions + summary
    Customer->>N8N: explicit confirmation
    N8N->>API: POST /order (confirmed items)
    API->>DB: persist order + reduce stock
    API-->>N8N: 201 order created (IN_CONFIRMATION)
    N8N-->>Customer: order confirmation
    loop every 10s
        UI->>API: GET /order/state (via CloudFront)
        API-->>UI: current orders
    end
    UI->>Operator: toast "New order #id"
    Operator->>UI: change state (PREPARATION → COMPLETED)
    UI->>API: PATCH /order/{id}?state=
```

Order states: `IN_CONFIRMATION` → `PREPARATION` → `COMPLETED`.

---

## 📦 Monorepo modules

| Path | Module | Status |
|---|---|---|
| `src/` | Spring Boot REST API (Java 21, Maven) | ✅ Complete |
| `ui-design/` | Admin UI React 19 + Vite + Tailwind 4 + shadcn/ui | ✅ Complete (MVP) |
| `infra/aws/frontend/` | Terraform: S3, CloudFront, IAM OIDC + operational scripts | ✅ Applied |
| `.github/workflows/` | Backend (Azure) and frontend (AWS) CI/CD | ✅ Active |
| `n8n/` | n8n workflow exports | ⚠️ Pending (runs externally) |
| `docker-compose.yml` | Local API + PostgreSQL | ✅ |

## ⚙️ Tech stack

| Layer | Technology | Purpose |
|---|---|---|
| Backend | Java 21, Spring Boot 3.4, Spring Data JPA/Hibernate | REST API and persistence |
| Backend | MapStruct, Lombok, Jakarta Validation, SpringDoc OpenAPI | Mapping, DTOs, docs |
| Backend | JUnit 5 + Mockito | Unit tests |
| Data | PostgreSQL 16 | Operational store |
| Frontend | React 19, Vite 8, TypeScript, Tailwind CSS 4, shadcn/ui, Biome | Admin UI |
| Automation | n8n + LLM agent *(external)* | WhatsApp conversation |
| Infra | Terraform, AWS S3 + CloudFront + IAM OIDC | Frontend static hosting |
| CI/CD | GitHub Actions | Checks + deploys |

---

## 📡 API — main endpoints

Full inventory in Swagger: `/swagger-ui.html` · OpenAPI: `/v3/api-docs`.

| Resource | Endpoints | Note |
|---|---|---|
| Products | `POST /products`, `PATCH /products/{id}/price`, `PATCH /products/{id}/units`, `DELETE /products/{id}` | ⚠️ **No list GET endpoint** (known gap) |
| Orders | `POST /order`, `GET /order/{id}`, `GET /order/user/{userId}`, `GET /order/state?state=`, `PATCH /order/{id}?state=`, `DELETE /order/{id}` | No global `GET /order`; the UI composes it with 3 per-state calls |
| Order Items | `POST /order-items`, `DELETE /order-items/{id}` | |
| Users | `POST /user`, `GET /user/{id}`, `GET /user/all`, `PUT /user/{id}`, `DELETE /user/{id}` | |

---

## ☁️ Frontend-only deploy on AWS

Only the **frontend** lives on AWS. Backend/n8n remain external.

- **Private S3**: hosts `ui-design/dist` (Vite build). No public access; CloudFront reads via Origin Access Control.
- **CloudFront**: HTTPS, global CDN, SPA fallback (403/404 → `index.html`) and **API proxy** — backend paths are routed to the external origin, avoiding CORS.
- **Vite build-time variables**: `VITE_MOCK_DATA` and `VITE_API_BASE_URL` are injected during `pnpm build` (they do not exist at runtime; see [`ui-design/README.md`](ui-design/README.md)).
- Full rationale (why not EC2/ECS/ECR, region, costs): [`infra/aws/frontend/README.md`](infra/aws/frontend/README.md).

### Deployment diagram

```mermaid
flowchart LR
    DEV["Developer<br/>push to develop"] --> GHA["GitHub Actions<br/>frontend-deploy.yml"]
    GHA -->|"OIDC role<br/>no AWS keys"| IAM["IAM role<br/>minimal deploy"]
    GHA -->|"pnpm build<br/>VITE_* injected"| DIST["ui-design/dist"]
    DIST -->|"aws s3 sync"| S3["Private S3"]
    GHA -->|invalidation| CF["CloudFront"]
    CF -->|OAC| S3
    CF -->|"API proxy"| BE["External backend<br/>Azure Web App"]
    U["Browser"] -->|HTTPS| CF
```

## 🔔 Notifications (MVP)

- **Today:** the Admin UI does **REST polling** every 10s against `GET /order/state` and shows toasts for new orders or state changes. No extra infrastructure.
- **Possible future:** SSE or WebSocket if the backend exposes them; polling would be replaced inside a single hook (`use-order-notifications.ts`).

---

## 🚀 Local quick start

### Backend + DB (Docker Compose)

```bash
docker-compose up --build
# API at http://localhost:8080 · Swagger at /swagger-ui.html
```

### Backend without Docker (in-memory H2)

```bash
mvn clean install
mvn spring-boot:run -Dspring.profiles.active=h2
```

### Frontend

```bash
cd ui-design
pnpm install
pnpm dev          # http://localhost:5173 (mock mode by default)
```

Real API mode and other frontend operations: [`ui-design/README.md`](ui-design/README.md).

### Backend tests

```bash
mvn test
```

---

## 🔁 CI/CD

```mermaid
flowchart LR
    PR["PR to develop"] --> CI["frontend-ci.yml<br/>biome + tsc + build"]
    PR --> BCI["pipeline.yml<br/>maven build + test"]
    MERGE["push / merge<br/>to develop"] --> DEPLOY["frontend-deploy.yml<br/>checks → build → S3 sync<br/>→ CloudFront invalidation"]
    MERGE --> BDEPLOY["pipeline.yml<br/>deploy jar to Azure"]
    DEPLOY -->|OIDC| AWS["AWS"]
    BDEPLOY -->|credentials| AZ["Azure"]
```

| Workflow | Trigger | Does |
|---|---|---|
| `pipeline.yml` | PR/push `main`/`develop` | Maven build, tests, deploy jar to Azure Web App |
| `frontend-ci.yml` | PR to `develop` (frontend paths) | `pnpm check` + `type-check` + `build` — no AWS |
| `frontend-deploy.yml` | Push to `develop` / manual | Checks → real build → OIDC → S3 sync → invalidation |

---

## ⚠️ Known risks and gaps

| Gap | Impact | Mitigation |
|---|---|---|
| No `GET /products` (list) | Agent/UI cannot list the catalog from the API | UI surfaces the gap; endpoint pending in backend |
| `OrderRequestDTO` lacks `userId` | Orders end up with no associated user | Documented; requires backend DTO change |
| n8n export not versioned in repo | Workflow not reproducible from the repo | `n8n/` reserved; JSON export pending |
| Global SPA fallback 403/404 | A real backend 403/404 through CloudFront returns `index.html` 200 | Documented in `infra/aws/frontend/main.tf` |
| 10s polling | Notification latency up to 10s + light API load | Acceptable for MVP; SSE/WS in the future |
| Local Terraform state | One infra operator at a time | Remote backend (S3 + lock) if the team grows |

---

## 📐 Visual diagram

- Image: [`media/architecture-diagram.png`](media/architecture-diagram.png) (embedded at the top of this README).
- Editable source: [`docs/architecture/deliverai-system-architecture.excalidraw`](docs/architecture/deliverai-system-architecture.excalidraw) (open at [excalidraw.com](https://excalidraw.com) → File → Open).

---

## 🙌 Team

- [Tulio Riaño Sánchez](https://github.com/tulio3101)
- [Julian Camilo Lopez Barrero](https://github.com/JulianLopez11)
- [Sergio Andrey Silva Rodriguez](https://github.com/OneCode182)
- [David Alejandro Patacon Henao](https://github.com/AlejandroHenao2572)
- [Manuel Alejandro Guarnizo](https://github.com/MAGG0059)

## 📄 License

Licensed under the **MIT License** — see [LICENSE](LICENSE).
