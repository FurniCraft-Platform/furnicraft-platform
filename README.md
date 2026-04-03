# 🪑 FurniCraft Platform

FurniCraft is a **microservices-based e-commerce backend platform** for furniture sales, built using **Spring Boot, Spring Cloud, and enterprise-level architecture patterns**.

This project is designed as a **portfolio-grade, production-like system**, focusing on scalability, clean architecture, and real-world backend practices.

---

## 🚀 Core Features

* 🔐 JWT-based Authentication & Authorization
* 🌐 API Gateway (centralized routing & security)
* 🧭 Service Discovery (Eureka)
* ⚙️ Centralized Configuration (Config Server)
* 🛒 Cart Management
* 📦 Order Processing
* 💳 Payment Flow (MVP level)
* 🧾 Product & Category Management
* 👤 User Profile & Address Management
* 🖼️ Media Service (MinIO)
* 📄 Unified Swagger (via API Gateway)

---

## 🏗️ Project Structure

```bash
furnicraft-platform/
├── gradle/
├── shared/
│   ├── common/        # ApiResponse, exception handling, filters
│   └── security/      # JWT, filters, security base config
│
├── infrastructure/
│   ├── api-gateway/   # Entry point, routing, security layer
│   └── eureka-server/ # Service registry
│
├── services/
│   ├── auth-service/        # Authentication & token management
│   ├── user-service/        # User profile & address
│   ├── product-service/     # Products & categories
│   ├── cart-service/        # Cart operations
│   ├── order-service/       # Order processing
│   ├── payment-service/     # Payment flow
│   └── media-service/       # File storage (MinIO)
```

---

## ⚙️ Tech Stack

* Java 21
* Spring Boot 3.4.x
* Spring Cloud 2024.x
* Spring Security (JWT)
* Spring Cloud Gateway
* Eureka Discovery Server
* OpenFeign
* PostgreSQL
* Liquibase
* MinIO
* Redis (planned)
* Docker & Docker Compose
* Gradle (multi-module)

---

## 🔐 Security Architecture

* Authentication handled by **auth-service**
* JWT token issued on login
* API Gateway enforces security (centralized)
* Internal service communication uses headers
* Role-based access control (RBAC)

> Services are **NOT exposed directly** — only accessible via Gateway

---

## 🌐 API Gateway (ENTRY POINT)

Base URL:

```
http://localhost:8080
```

### Routes

| Service  | Route Prefix        |
| -------- | ------------------- |
| Auth     | /api/v1/auth/**     |
| Users    | /api/v1/users/**    |
| Products | /api/v1/products/** |
| Cart     | /api/v1/carts/**    |
| Orders   | /api/v1/orders/**   |
| Payments | /api/v1/payments/** |
| Media    | /api/v1/media/**    |

---

## 📄 Swagger (Aggregated)

Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

### Features

* All services combined under one UI
* Works fully through API Gateway
* No CORS issues (gateway-based routing)
* "Try it out" fully functional

---

## 🧪 End-to-End Test Flow

### 1. Register

```
POST /api/v1/auth/register
```

### 2. Login

```
POST /api/v1/auth/login
```

Get:

```json
{
  "access_token": "...",
  "refresh_token": "..."
}
```

---

### 3. Use Token

Header:

```
Authorization: Bearer <access_token>
```

---

### 4. Test Flow

1. Create Address
2. Get Products
3. Add to Cart
4. Create Order
5. Make Payment

---

## 🗄️ Database Design

Each service has its own database:

* furnicraft_auth
* furnicraft_user
* furnicraft_product
* furnicraft_cart
* furnicraft_order
* furnicraft_payment
* furnicraft_media

> Fully isolated microservice database architecture

---

## 🐳 Docker Setup

```bash
docker-compose up -d
```

Includes:

* PostgreSQL
* Redis
* MinIO

---

## ▶️ Run Order (Local Development)

Start services in this order:

1. eureka-server
2. config-server
3. api-gateway
4. all business services

---

## 📦 Build

```bash
./gradlew clean build
```

---

## 🧠 Key Design Decisions

* Gateway-first security model
* Microservice isolation (DB per service)
* Shared modules for consistency
* DTO-based inter-service communication
* Liquibase for DB versioning
* Clean layered architecture

---

## ⚠️ MVP Scope (Current State)

* Payment is simplified
* No frontend (API-first approach)
* Some validations are minimal
* No async messaging yet

---

## 🔮 Future Improvements

* Kafka (event-driven architecture)
* Redis (caching & token blacklist)
* Admin panel (dashboard)
* Advanced payment integration
* Monitoring (Prometheus + Grafana)

---
## 👨‍💻 Authors
*  **Ali Huseynov** - [Backend Developer](https://github.com/alihsynv)
*  **Javid Rzayev** - [Backend Developer](https://github.com/JavidTheCode)

---

## 🎯 Project Goal

This project demonstrates:

* enterprise backend architecture
* microservices design
* secure API development
* real-world system thinking

---
