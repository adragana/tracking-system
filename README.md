# Shipment Tracking System

A backend REST API for managing and tracking shipments throughout their delivery lifecycle. The system allows employees to create shipments, update statuses, view full status history, filter shipments, and import data from CSV or Excel files.

Built with **Spring Boot** and **PostgreSQL**, containerized with **Docker Compose** .

---

## Features

- **Shipment management** — create, update, cancel, and delete shipments
- **Status lifecycle tracking** — shipments move through `CREATED` → `IN_TRANSIT` → `DELIVERED` or `CANCELLED`
- **Status history** — every status change is recorded with timestamp and optional note
- **User management** — each user can have one or more shipments
- **File Import** — upload multiple shipments via CSV (`.csv`) or Excel (`.xlsx`) files
- **Filtering & pagination** — search shipments by user, status, and creation date range
- **Public tracking** — anyone can look up a shipment by tracking number (no authentication required)
- **JWT authentication** — secure access for employees
- **API documentation** — interactive Swagger UI
- **Database migrations** — schema managed with Flyway

---

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 4.1.0 |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Migrations | Flyway |
| Security | Spring Security + JWT |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| CSV parsing | Apache Commons CSV |
| Excel parsing | Apache POI |
| Build tool | Maven |
| Containerization | Docker + Docker Compose |

---

## Requirements

### Option 1: Run with Docker

- [Docker Engine](https://docs.docker.com/engine/install/)
- [Docker Compose](https://docs.docker.com/compose/install/) 

### Option 2: Run locally without Docker
- Java 21
- Maven 3.9+ (or use the included Maven Wrapper: ./mvnw)
- PostgreSQL 16 - locally

---

### Getting Started
1. #### Clone the repository:
   #### git clone https://github.com/YOUR_GITHUB_USERNAME/tracking-system.git
   #### cd tracking-system
2. #### Environment variables
   All configuration is driven by environment variables. Copy the example file (.env.example) for local overrides:

| Variable         | Description                                              | Default value                                                                                                 |
|----------------|----------------------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| DB_URL         | JDBC connection string for PostgreSQL                   | jdbc:postgresql://localhost:5432/tracking_system |
| DB_USERNAME    | Database username                                        | postgres                                                                                                      |
| DB_PASSWORD    | Database password                                        | postgres                                                                                                      |
| JWT_SECRET     | Base64-encoded secret key for signing JWT tokens        | `.env.example`                                                                                                |
| JWT_EXPIRATION | JWT token lifetime in milliseconds                      | 86400000 (24 hours)                                                                                           |
- Note: When running with Docker Compose, DB_URL must use the Docker service name postgres instead of localhost: jdbc:postgresql://postgres:5432/tracking_system

## Running with Docker Compose

Start the entire system (PostgreSQL + backend application) with a single command:

```
docker compose up --build
```

### API Access

After starting the application, the API will be available at:

| Service     | URL                                                |
|------------|----------------------------------------------------|
| API base URL | http://localhost:8080                             |
| Swagger UI   | http://localhost:8080/swagger-ui/index.html      |

### Docker commands
-  Build and start
```
docker compose up --build
```
- Start in background (detached mode)
```
docker compose up --build -d
```
- Stop containers
```
docker compose down
```
- Stop containers and delete database data
```
docker compose down -v
```


## Running Locally (without Docker)

### 1. Create the database

```sql
CREATE DATABASE tracking_system;
```

### 2. Configure environment variables

Either set environment variables in your shell/IDE, or create a `.env` file and load it via the IntelliJ EnvFile plugin.

For local development, use:

```env
DB_URL=jdbc:postgresql://localhost:5432/tracking_system
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=your-base64-encoded-secret-key-here
JWT_EXPIRATION=86400000
```

### 3. Build and run

Using Maven Wrapper:

```bash
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

Or using installed Maven:

```bash
mvn clean package -DskipTests
mvn spring-boot:run
```

The application starts on `http://localhost:8080`. Flyway migrations run automatically on startup.

---

## Authentication

Most endpoints require a valid JWT token. Authentication flow:

### 1. Register an employee (public)

```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

### 2. Login (public)

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

Response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "john@example.com",
  "name": "John Doe"
}
```

### 3. Use the token

Include the token in the `Authorization` header for all protected endpoints:

```
Authorization: Bearer <your-jwt-token>
```

### Public endpoints (no authentication required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Employee login |
| POST | `/api/auth/register` | Employee registration |
| GET | `/api/shipments/tracking/{trackingNumber}` | Track shipment by tracking number |
| GET | `/swagger-ui/**` | Swagger UI |
| GET | `/v3/api-docs/**` | OpenAPI specification |

---

## API Endpoints

Full interactive documentation is available at `http://localhost:8080/swagger-ui/index.html`.

### Users

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/users` | Required | Create a new user |
| GET | `/api/users` | Required | Get all users |
| GET | `/api/users/{id}` | Required | Get user by ID |
| PUT | `/api/users/{id}` | Required | Update user |
| DELETE | `/api/users/{id}` | Required | Delete user |

Create user request body:

```json
{
  "name": "Jane Smith",
  "email": "jane@example.com",
  "phone": "+381601234567"
}
```

### Shipments

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/shipments` | Required | Create a shipment |
| GET | `/api/shipments` | Required | Search/filter shipments (paginated) |
| GET | `/api/shipments/{id}` | Required | Get shipment by ID |
| PATCH | `/api/shipments/status/{id}` | Required | Update shipment status |
| PATCH | `/api/shipments/cancel/{id}` | Required | Cancel shipment by ID |
| DELETE | `/api/shipments/{id}` | Required | Delete shipment |
| POST | `/api/shipments/import` | Required | Bulk import from CSV/Excel |
| GET | `/api/shipments/tracking/{trackingNumber}` | Public | Get shipment by tracking number |
| PATCH | `/api/shipments/tracking/status/{trackingNumber}` | Required | Update status by tracking number |
| PATCH | `/api/shipments/tracking/cancel/{trackingNumber}` | Required | Cancel by tracking number |

Create shipment request body:

```json
{
  "userId": 1,
  "trackingNumber": "TRK-001",
  "description": "Electronics package",
  "deliveryAddress": "123 Main St, Belgrade"
}
```

Update status request body:

```json
{
  "newStatus": "IN_TRANSIT",
  "note": "Package picked up by courier"
}
```

Search/filter query parameters:

| Parameter | Type | Description |
|-----------|------|-------------|
| `userId` | Long | Filter by user ID |
| `status` | String | Filter by status (`CREATED`, `IN_TRANSIT`, `DELIVERED`, `CANCELLED`) |
| `createdFrom` | Date (ISO) | Filter shipments created on or after this date (`yyyy-MM-dd`) |
| `createdTo` | Date (ISO) | Filter shipments created on or before this date (`yyyy-MM-dd`) |
| `page` | Integer | Page number (default: 0) |
| `size` | Integer | Page size (default: 20) |

Example:

```
GET /api/shipments?userId=1&status=IN_TRANSIT&createdFrom=2025-01-01&page=0&size=10
```

### Shipment Statuses

| Status | Description |
|--------|-------------|
| `CREATED` | Shipment has been created |
| `IN_TRANSIT` | Shipment is in transport |
| `DELIVERED` | Shipment has been successfully delivered |
| `CANCELLED` | Shipment has been cancelled |

Every status change is stored in the `shipment_status_history` table with timestamp and optional note.

Valid transitions:

- `CREATED` → `IN_TRANSIT` or `CANCELLED`
- `IN_TRANSIT` → `DELIVERED` or `CANCELLED`
- `DELIVERED` and `CANCELLED` are final statuses and cannot be changed

---

## Import (CSV / Excel)

Upload a file to import multiple shipments at once:

```http
POST /api/shipments/import
Content-Type: multipart/form-data
Authorization: Bearer <token>

file: <your-file.csv or your-file.xlsx>
```

### Supported formats

- CSV (`.csv`)
- Excel (`.xlsx`)

### Required columns

| Column | Required | Description |
|--------|----------|-------------|
| `trackingNumber` | Yes | Unique tracking number |
| `description` | Yes | Shipment description |
| `userEmail` | Yes | Email of an existing user |
| `status` | No | Initial status (defaults to `CREATED`) |
| `deliveryAddress` | No | Delivery address |

### CSV example

```csv
trackingNumber,description,userEmail,status,deliveryAddress
TRK-100,Package A,jane@example.com,CREATED,Adress1
TRK-101,Package B,jane@example.com,IN_TRANSIT,Address2
TRK-102,Package C,john@example.com,CREATED,Address3
```


---

## Project Structure

```
tracking-system/
├── src/
│   ├── main/
│   │   ├── java/org/spring/trackingsystem/
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── service/          # Business logic
│   │   │   ├── repository/       # Data access layer
│   │   │   ├── entity/           # JPA entities
│   │   │   ├── dto/              # Request/response objects
│   │   │   ├── security/         # Security configuration
│   │   │   ├── specification/    # JPA Specifications for filtering
│   │   │   ├── exception/        # Exception handling
│   │   │   └── Util/             # JWT utilities
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/     # Flyway SQL migrations
│   └── test/                     # Unit and integration tests
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── .env.example
├── pom.xml
└── README.md
```

---

## Database Schema

Managed by Flyway migrations in `src/main/resources/db/migration/`:

| Migration | Description |
|-----------|-------------|
| `V1__init_schema.sql` | Creates `users`, `shipments`, `shipment_status_history` tables |
| `V2__added_delivery_address.sql` | Adds `delivery_address` column to `shipments` |
| `V3__employee.sql` | Creates `employees` table for authentication |