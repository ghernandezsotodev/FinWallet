
# FinWallet — Full-Stack Banking Application

A full-stack virtual wallet application built with **Java 17, Spring Boot 3, Angular, and PostgreSQL**. FinWallet supports deposits, withdrawals, and transfers, with a strong focus on transaction consistency, access control, and secure financial operations.

##  Features

### Backend & Security

- **JWT Authentication:** Authentication and authorization using Spring Security and JSON Web Tokens.
- **Transaction Locking:** Pessimistic write locking with `PESSIMISTIC_WRITE` to prevent race conditions and maintain account balance consistency.
- **Business Validations:** Prevents insufficient funds, negative transaction amounts, unauthorized access, and transfers to the same account.
- **REST API:** Endpoints for account management and financial transactions.
- **Security Scanning:** Trivy scans for known vulnerabilities in dependencies and container images.
- **Dependency Management:** Dependency updates and Maven BOM overrides for components such as Jackson and embedded Tomcat.

### Frontend

- **Angular Signals:** Reactive state management and efficient UI updates.
- **Standalone Components:** Modern Angular architecture using standalone components.
- **Routing & HTTP Interceptors:** Lazy-loaded routes, route-based navigation, and centralized HTTP request handling.
- **Responsive UI:** Custom SCSS styling for account balances, transaction history, forms, and confirmation modals.

## Tech Stack

| Technology                  | Purpose                            |
|-----------------------------|------------------------------------|
| Java 17                     | Backend programming language       |
| Spring Boot 3               | Backend application framework      |
| Spring Security & JWT       | Authentication and authorization   |
| Spring Data JPA & Hibernate | Persistence and ORM                |
| PostgreSQL 17               | Relational database                |
| Angular 22                  | Frontend framework                 |
| TypeScript & SCSS           | Frontend development and styling   |
| Docker & Docker Compose     | Containerization and orchestration |
| Nginx                       | Web server and frontend delivery   |
| Trivy                       | Vulnerability scanning             |

## Getting Started

### Prerequisites

Make sure the following tools are installed:

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

### Installation & Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/ghernandezsotodev/FinWallet.git
   ```

2. Navigate to the project directory:

   ```bash
   cd FinWallet
   ```

3. Build and start the application:

   ```bash
   docker compose up --build -d
   ```

### Application URLs

Once all containers are running, access the application using the following URLs:

| Service     | URL                                                                            |
|-------------|--------------------------------------------------------------------------------|
| Frontend    | [http://localhost:4200](http://localhost:4200)                                 |
| Backend API | [http://localhost:8080/api](http://localhost:8080/api)                         |
| Swagger UI  | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |

### Stop the Application

To stop the containers, run:

```bash
docker compose down
```

To stop the containers and remove the associated volumes as well, run:

```bash
docker compose down -v
```

> **Warning:** The `-v` flag removes associated volumes and may permanently delete persisted database data.

## Security & Transaction Integrity

FinWallet incorporates multiple safeguards to improve the security and consistency of financial operations.

- **Authentication:** JWT-based authentication integrated with Spring Security.
- **Access Control:** Authorization checks to prevent unauthorized account access.
- **Concurrency Control:** Pessimistic database locking to prevent concurrent transactions from causing inconsistent account balances.
- **Transaction Validation:** Validates transaction amounts, available funds, account ownership, and transfer destinations.
- **Vulnerability Management:** Trivy scans for known vulnerabilities in dependencies and container images.
- **Dependency Security:** Dependency updates and Maven BOM overrides for components such as Jackson and embedded Tomcat.

> **Note:** Vulnerability scan results depend on the scan date, the versions being analyzed, and the vulnerability database available at the time of scanning.

## Author

**Gonzalo Hernández**

Backend Software Engineer

*Java · Spring Boot · PHP · Laravel*

- **GitHub:** [@ghernandezsotodev](https://github.com/ghernandezsotodev)

---

<p style="text-align: center">
  Built with Java, Spring Boot, Angular, and PostgreSQL.
</p>
