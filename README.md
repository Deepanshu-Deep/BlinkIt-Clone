# BlinkIt Clone — Backend REST API

A full-featured **grocery delivery backend** inspired by BlinkIt, built with **Spring Boot 3.4**, **MySQL**, **Redis**, and **Apache Kafka**. It exposes a RESTful API for user authentication, product browsing, cart management, and order lifecycle management — secured with JWT-based authentication and role-based access control.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Local Setup](#local-setup)
- [Environment Variables](#environment-variables)
- [API Reference](#api-reference)
- [Roles and Access Control](#roles-and-access-control)
- [Kafka Events](#kafka-events)
- [Redis Caching](#redis-caching)
- [Database Schema Overview](#database-schema-overview)
- [License](#license)

---

## Features

- **JWT Authentication** — Secure sign-up, sign-in, and password reset
- **Role-based Access Control** — `ADMIN` and `USER` roles with method-level security
- **Product Management** — CRUD, category filtering, keyword search with pagination
- **Cart System** — Add/update/remove items, persistent cart per user
- **Order Lifecycle** — Place → Ship → Deliver → Cancel with status tracking
- **Apache Kafka** — Async order event publishing and consuming via `OrderProducer` / `OrderConsumer`
- **Redis Caching** — TTL-based response caching (600s default) for performance
- **Global Exception Handling** — Structured error responses across all endpoints
- **Password Encoder** — BCrypt hashing for secure credential storage
- **DevTools** — Hot reload during development

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.4.0 |
| Language | Java 17 |
| Database | MySQL 8+ |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT (JJWT 0.11.5) |
| Messaging | Apache Kafka |
| Caching | Redis (Spring Data Redis) |
| Build Tool | Maven |
| Utilities | Lombok, ModelMapper, Apache Commons Lang3 |

---

## Project Structure

```
src/main/java/com/grocery/
├── config/                  # CORS, JWT filter, Kafka, Redis, Security config
├── controller/              # REST controllers (Auth, Product, Cart, Order, User, Admin)
├── dto/                     # Data Transfer Objects
├── enums/                   # RoleType, StatusType, PaymentMethodType, PaymentStatusType
├── exception/               # Custom exceptions + GlobalExceptionHandlerClass
├── kafka/                   # OrderProducer & OrderConsumer
├── model/                   # JPA Entities (User, Product, Category, Cart, Order, Payment)
├── repository/              # Spring Data JPA Repositories
├── security/                # CustomUserDetails
├── service/                 # Service interfaces
├── serviceImpl/             # Service implementations
└── BlinkItCloneProjectApplication.java
```

---

## Prerequisites

Make sure the following are installed before setting up the project:

| Tool | Version | Download |
|---|---|---|
| Java JDK | 17+ | https://adoptium.net |
| Maven | 3.8+ | https://maven.apache.org |
| MySQL | 8.0+ | https://dev.mysql.com/downloads |
| Apache Kafka | 3.x | https://kafka.apache.org/downloads |
| Redis | 7.x | https://redis.io/download |
| Git | Latest | https://git-scm.com |

---

## Local Setup

Follow these steps in order to get the project running on your machine.

### 1. Clone the Repository

```bash
git clone https://github.com/Deepanshu-Deep/BlinkIt-Clone.git
cd BlinkIt-Clone
```

### 2. Create the MySQL Database

Log into MySQL and create the database:

```sql
CREATE DATABASE blink;
```

> The schema tables are auto-created by Hibernate on first run (`spring.jpa.hibernate.ddl-auto=update`).

### 3. Start Redis

```bash
# Linux / macOS
redis-server

# Windows (via WSL2 or Chocolatey)
redis-server
```

Verify Redis is running:

```bash
redis-cli ping
# Expected: PONG
```

### 4. Start Apache Kafka

Kafka requires ZooKeeper. Open two separate terminal windows:

**Terminal 1 — Start ZooKeeper:**
```bash
# From your Kafka installation directory
bin/zookeeper-server-start.sh config/zookeeper.properties

# Windows
bin\windows\zookeeper-server-start.bat config\zookeeper.properties
```

**Terminal 2 — Start Kafka Broker:**
```bash
bin/kafka-server-start.sh config/server.properties

# Windows
bin\windows\kafka-server-start.bat config\server.properties
```

### 5. Configure Environment Variables

Create a `.env` file in the project root (same level as `pom.xml`) if it doesn't already exist:

```env
DB_URL=jdbc:mysql://localhost:3306/blink
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=your_very_long_and_secure_jwt_secret_key_here
```

### 6. Build the Project

```bash
mvn clean install -DskipTests
```

### 7. Run the Application

```bash
mvn spring-boot:run
```

Or run the generated JAR directly:

```bash
java -jar target/Blink-It-clone-Project-0.0.1-SNAPSHOT.jar
```

The server starts on **port `8826`** by default.

```
http://localhost:8826
```

---

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | JDBC URL for MySQL | `jdbc:mysql://localhost:3306/blink` |
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | `root` |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker address | `localhost:9092` |
| `REDIS_HOST` | Redis host | `localhost` |
| `REDIS_PORT` | Redis port | `6379` |
| `JWT_SECRET` | Base64-encoded secret for signing JWT tokens | *(required — set a strong key in production)* |

> **Never commit your `.env` file or real secrets to version control.** The `.gitignore` already excludes it.

---

## API Reference

All endpoints are prefixed with `/api`. Protected routes require a `Bearer <token>` header.

### Authentication — `/api/auth`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/auth/signup` | Public | Register a new user |
| `POST` | `/api/auth/signin` | Public | Login and receive JWT token |
| `POST` | `/api/auth/reset-password` | Public | Reset password using email + phone |

**Signup Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secret123",
  "phoneNumber": "9876543210"
}
```

**Signin Request Body:**
```json
{
  "email": "john@example.com",
  "password": "secret123"
}
```

**Response (JWT):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "..."
}
```

---

### Products — `/api/products`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/products` | USER | Get all products (paginated) |
| `GET` | `/api/products/{id}` | USER | Get product by ID |
| `GET` | `/api/products/category/{name}` | USER | Get products by category |
| `GET` | `/api/products/search?q={keyword}` | USER | Search products by keyword |
| `POST` | `/api/products` | ADMIN | Add a new product |
| `PUT` | `/api/products/{id}` | ADMIN | Update a product |
| `DELETE` | `/api/products/{id}` | ADMIN | Delete a product |

---

### Cart — `/api/cart`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/cart` | USER | View current user's cart |
| `POST` | `/api/cart/add` | USER | Add item to cart |
| `PUT` | `/api/cart/update` | USER | Update item quantity |
| `DELETE` | `/api/cart/remove/{itemId}` | USER | Remove item from cart |

---

### Orders — `/api/orders`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/orders/place` | USER | Place order from cart |
| `GET` | `/api/orders/my` | USER | Get all orders for current user |
| `GET` | `/api/orders/my/{id}` | USER | Get a specific order (own only) |
| `GET` | `/api/orders/{id}` | USER | Get order by ID |
| `GET` | `/api/orders` | ADMIN | Get all orders (paginated) |
| `PUT` | `/api/orders/{id}` | ADMIN | Update order details |
| `PUT` | `/api/orders/{id}/ship` | ADMIN | Mark order as shipped |
| `PUT` | `/api/orders/{id}/deliver` | ADMIN | Mark order as delivered |
| `PUT` | `/api/orders/{id}/cancel` | ADMIN | Cancel an order |
| `DELETE` | `/api/orders/{id}` | ADMIN | Delete an order |

---

### Categories — `/api/categories`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/categories` | USER | List all categories |
| `POST` | `/api/categories` | ADMIN | Create a new category |
| `PUT` | `/api/categories/{id}` | ADMIN | Update a category |
| `DELETE` | `/api/categories/{id}` | ADMIN | Delete a category |

---

### Users — `/api/users`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/users/me` | USER | Get current user profile |
| `PUT` | `/api/users/{id}` | USER | Update user profile |

---

### Admin — `/api/admin`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/admin/users` | ADMIN | List all users |
| `DELETE` | `/api/admin/users/{id}` | ADMIN | Delete a user |

---

## Roles and Access Control

The application uses two roles:

- **`ROLE_USER`** — Registered customers. Can browse products, manage their own cart, place orders, and view their own order history.
- **`ROLE_ADMIN`** — Administrators. Can manage products, categories, all orders, and all users.

Role is assigned at registration and enforced via `@PreAuthorize` annotations on controller methods.

---

## Kafka Events

Order events are published asynchronously using Apache Kafka:

- **Topic:** `order-events` (configured in `KafkaConfig.java`)
- **Producer:** `OrderProducer` — publishes an event when an order is placed
- **Consumer:** `OrderConsumer` — listens to the topic and processes order notifications (e.g., sending emails via `NotificationService`)

**Consumer Group ID:** `grocery-group`

---

## Redis Caching

Redis is used to cache frequently accessed data and reduce database load:

- **Cache type:** Redis
- **Default TTL:** 600,000 ms (10 minutes)
- **Host/Port:** Configurable via environment variables

Cache is configured in `RedisConfig.java` and enabled via Spring's `@EnableCaching`.

---

## Database Schema Overview

The core entities and their relationships:

```
User ──────────────── Cart
  |                     |
  |                  CartItem ──── Product ──── Category
  |
  └──────────────── Order ──────── OrderItem ── Product
                      |
                    Payment
```

- A `User` has one `Cart` and many `Orders`
- A `Cart` has many `CartItems`, each linked to a `Product`
- An `Order` has many `OrderItems` and one `Payment`
- A `Product` belongs to a `Category`


---

> Built with Spring Boot, MySQL, Redis & Kafka.
