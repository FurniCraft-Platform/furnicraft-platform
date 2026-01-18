# 🛋️ FurniCraft Platform

FurniCraft is a scalable e-commerce backend ecosystem specifically designed for modern furniture retail, built on a robust microservices architecture.

## 🚀 Project Vision
The goal of this project is to provide a high-performance, resilient backend infrastructure featuring asynchronous communication and distributed transaction management using the Saga Pattern.

## 🏗️ Technology Stack
* **Language:** Java 21 (LTS)
* **Framework:** Spring Boot 4.0.x (Latest Stable)
* **Microservices:** Spring Cloud (Eureka, Config Server, API Gateway)
* **Messaging:** Apache Kafka (Event-driven architecture)
* **Database:** PostgreSQL (Database-per-service pattern)
* **Cache:** Redis (Security token blacklisting & performance caching)
* **Containerization:** Docker & Docker Compose

## 📂 Project Structure

- **`infrastructure/`**: The backbone of the system containing Service Discovery, API Gateway, and Centralized Configuration.
- **`services/`**: Core business logic modules including Auth, Order, Product, Inventory, and more.
- **`shared/`**: Common libraries shared across services (Security filters, Event schemas, Global DTOs).
- **`docs/`**: Architecture diagrams, API specifications (OpenAPI), and implementation guides.

## 👥 The Team

* 👨‍💻 **Ali Huseynov** - [Backend Developer](https://github.com/alihsynv)
* 👨‍💻 **Javid Rzayev** - [Backend Developer](https://github.com/JavidTheCode)