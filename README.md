# 🚀 DeliverAI

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4-brightgreen?logo=spring)
![Maven](https://img.shields.io/badge/Maven-3.9-blue?logo=apachemaven)
![Spring Data JPA](https://img.shields.io/badge/JPA-Hibernate-6DB33F?logo=hibernate)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3.0-green?logo=swagger)
![MapStruct](https://img.shields.io/badge/MapStruct-1.5-purple)
![Lombok](https://img.shields.io/badge/Lombok-1.18-red?logo=lombok)
![JUnit](https://img.shields.io/badge/JUnit-5.11-blue?logo=junit5)
![Mockito](https://img.shields.io/badge/Mockito-5.14-green)
![License](https://img.shields.io/badge/License-MIT-yellow)
![Built by](https://img.shields.io/badge/built_by-Equipo_DeliverAI-blue)

**DeliverAI** is an academic project developed for the course **Hyperautomation Architectures: Design, Implementation and Governance of AI Agents in Business Contexts** at the **Colombian School of Engineering Julio Garavito** (intersemester period 2026-I).

The goal is to model and automate the complete ordering flow used by small and medium businesses (SMBs) when customers place delivery orders via WhatsApp. Think of a local bakery: you text the business on WhatsApp, interact with a person, and place your order. **DeliverAI automates the first contact phase** using AI agents, RPA, and MCPs orchestrated in **n8n**. Once the order is confirmed, an HTTP request hits the **REST API** to persist the data. A future **Admin UI** will give business owners full control over their products, orders, and live tracking.

---

## 📖 Overview

```
Customer (WhatsApp)  →  n8n (Agents / RPA / MCPs)  →  REST API  →  Database
                                                             ↘  Admin UI (future)
```

DeliverAI bridges the gap between conversational AI and order management:

- **WhatsApp Automation** — AI agents understand customer requests, suggest products, confirm quantities, and validate the order — all through natural conversation.
- **Order Engine** — Once confirmed, the order is sent to a Spring Boot REST API that handles CRUD for products, orders, and order items with proper validation and error handling.
- **Business Dashboard** *(coming soon)* — A web interface for business owners to manage inventory, view incoming orders, and update order statuses.

---

## 🏗️ Architecture

The system is split into three modules:

| Module | Status | Description |
|--------|--------|-------------|
| **n8n Automation** | ⚠️ Coming soon | WhatsApp integration layer using AI agents, RPA bots, and MCP servers. Workflow exports will live in `n8n/`. |
| **REST API** | ✅ Complete | Spring Boot backend — handles products, orders, and order items. Full Swagger documentation. |
| **Admin UI** | ⚠️ Coming soon | Administrative dashboard for business owners. Design specs will live in `ui-design/`. |

*Architecture diagram coming soon.*

### Data Flow

1. Customer sends a WhatsApp message to the business number.
2. n8n workflow receives the message and uses an **AI agent** to interpret the request.
3. The agent interacts with the customer via RPA/MCP to clarify items, quantities, and confirm the order.
4. On confirmation, n8n sends an HTTP POST to the REST API with the order data.
5. The API validates, persists, and returns the created order.
6. The business owner can later manage orders through the Admin UI.

---

## ⚙️ Tech Stack

| Technology | Purpose |
|------------|---------|
| **Java 21** | Language |
| **Spring Boot 3.4** | Framework |
| **Maven 3.9** | Build tool |
| **Spring Data JPA / Hibernate** | ORM & persistence |

| **Lombok** | Boilerplate reduction |
| **MapStruct 1.5** | Object mapping (Entity ↔ DTO) |
| **SpringDoc OpenAPI (Swagger)** | API documentation |
| **JUnit 5 + Mockito** | Unit testing |
| **Jakarta Validation** | Request validation |
| **n8n** *(future)* | Workflow automation |
| **React** *(future)* | Admin UI |

---

## 📦 REST API (Backend)

The API follows a layered architecture:

```
Controller  →  Service  →  Repository  →  Database
     ↕
   Mapper
     ↕
    DTO
```

### Domain Model

```text
┌───────────┐     ┌───────────┐     ┌──────────────┐
│  Product  │     │   Order   │     │  OrderItem   │
├───────────┤     ├───────────┤     ├──────────────┤
│ id        │     │ id        │     │ id           │
│ name      │     │ orderDate │     │ order (FK)   │
│ units     │     │ state     │     │ product (FK) │
│ price     │     │ subTotal  │     │ quantity     │
└───────────┘     │ items     │     └──────────────┘
                  └───────────┘
```

**State enum** — tracks the order lifecycle:

| State | Description |
|-------|-------------|
| `IN_CONFIRMATION` | Awaiting customer confirmation |
| `PREPARATION` | Being prepared by the business |
| `COMPLETED` | Finished |
| *(pending)* | `READY_FOR_PICKUP` — TODO |
| *(pending)* | `IN_DELIVERY` — TODO |

### Project Structure

```
src/main/java/edu/eci/ahia/
├── DeliverAiApplication.java          # Entry point
├── config/
│   ├── CorsConfig.java                # CORS configuration
│   └── SwaggerConfig.java             # OpenAPI documentation setup
├── controller/
│   ├── OrderController.java           # Order endpoints
│   ├── OrderItemController.java       # Order item endpoints
│   └── ProductController.java         # Product endpoints
├── exception/
│   ├── GlobalExceptionHandler.java    # Centralized error handling
│   ├── InsufficientStockException.java
│   ├── OrderItemNotFoundException.java
│   ├── OrderNotFoundException.java
│   └── ProductNotFoundException.java
├── mapper/
│   ├── OrderMapper.java
│   ├── OrderItemMapper.java
│   └── ProductMapper.java
├── model/
│   ├── dto/request/                   # Request DTOs
│   │   ├── OrderItemRequestDTO.java
│   │   ├── OrderRequestDTO.java
│   │   └── ProductRequestDTO.java
│   ├── dto/response/                  # Response DTOs
│   │   ├── OrderItemResponseDTO.java
│   │   ├── OrderResponseDTO.java
│   │   └── ProductResponseDTO.java
│   └── entity/
│       ├── Order.java
│       ├── OrderItem.java
│       ├── Product.java
│       └── enums/State.java
├── repository/
│   ├── OrderItemRepository.java
│   ├── OrderRepository.java
│   └── ProductRepository.java
└── service/
    ├── OrderItemService.java
    ├── OrderService.java
    └── ProductService.java
```

---

## 🔌 n8n Module

> ⚠️ **Coming soon** — Not included in this branch.

This module will contain the WhatsApp automation layer using **AI agents**, **RPA bots**, and **MCP servers** orchestrated in **n8n**. Workflow JSON exports and configuration files will be stored in the [`n8n/`](n8n/) directory.

The n8n workflows will handle:
- Incoming WhatsApp message parsing
- AI agent conversation with the customer (product selection, quantity, address, etc.)
- Order confirmation flow
- HTTP request to the REST API to persist confirmed orders

---

## 🖥️ Admin UI

> ⚠️ **Coming soon** — Not included in this branch.

An administrative dashboard that allows business owners to:
- View incoming orders in real time
- Manage product catalog (add, edit, delete products)
- Update order statuses (confirm, prepare, complete, deliver)
- Track order history

Design assets and specifications will be stored in the [`ui-design/`](ui-design/) directory.

---

## 🧪 Testing

The project includes unit tests for controllers and services using **JUnit 5** and **Mockito**:

```
src/test/java/edu/eci/ahia/
├── controller/
│   ├── OrderControllerTest.java
│   ├── OrderItemControllerTest.java
│   └── ProductControllerTest.java
└── service/
    ├── OrderServiceTest.java
    ├── OrderItemServiceTest.java
    └── ProductServiceTest.java
```

Run tests with:

```bash
mvn test
```

---

## 🚀 Quick Start

### Prerequisites

- **JDK 21** — [Download](https://jdk.java.net/21/)
- **Maven 3.9+** — [Download](https://maven.apache.org/download.cgi)

### Run

```bash
# Clone the repository
git clone https://github.com/tulio3101/DeliverAI.git
cd DeliverAI

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

### Swagger UI

Once the application is running, access the interactive API documentation:

```
http://localhost:8080/swagger-ui.html
```

---

## 📡 API Endpoints

### Products

| Method | Path | Body | Description |
|--------|------|------|-------------|
| `POST` | `/products` | `ProductRequestDTO` | Create a new product |
| `PATCH` | `/products/{id}/price` | `?price=...` | Update product price |
| `PATCH` | `/products/{id}/units` | `?units=...` | Update product stock |
| `DELETE` | `/products/{id}` | — | Delete a product |

**ProductRequestDTO:**
```json
{ "name": "Chocolate Cake", "units": 10, "price": 25.00 }
```

### Orders

| Method | Path | Body | Description |
|--------|------|------|-------------|
| `POST` | `/order` | `OrderRequestDTO` | Create a new order |
| `PATCH` | `/order/{id}` | `?state=...` | Update order state |
| `DELETE` | `/order/{id}` | — | Delete an order |

**OrderRequestDTO:**
```json
{
  "subTotal": 50.00,
  "orderItems": [
    { "product": { "name": "Cake", "units": 1, "price": 25.00 }, "quantity": 2 }
  ]
}
```

### Order Items

| Method | Path | Body | Description |
|--------|------|------|-------------|
| `POST` | `/order-items` | `OrderItemRequestDTO` | Add item to an order |
| `DELETE` | `/order-items/{id}` | — | Remove item from an order |

---

## 🙌 Team

| | |
|---|---|
| <img src="https://github.com/tulio3101.png?size=40" width="20" alt=""/> | [Tulio Riaño Sánchez](https://github.com/tulio3101) |
| <img src="https://github.com/JulianLopez11.png?size=40" width="20" alt=""/> | [Julian Camilo Lopez Barrero](https://github.com/JulianLopez11) |
| <img src="https://github.com/OneCode182.png?size=40" width="20" alt=""/> | [Sergio Andrey Silva Rodriguez](https://github.com/OneCode182) |
| <img src="https://github.com/AlejandroHenao2572.png?size=40" width="20" alt=""/> | [David Alejandro Patacon Henao](https://github.com/AlejandroHenao2572) |
| <img src="https://github.com/MAGG0059.png?size=40" width="20" alt=""/> | [Manuel Alejandro Guarnizo](https://github.com/MAGG0059) |

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.
