# 📦 Coagronet Backend

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.12-brightgreen)
![Java](https://img.shields.io/badge/Java-25-orange)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15.14-blue)
![License](https://img.shields.io/badge/License-Proprietary-red)

Coagronet is a robust inventory and resource management system designed for agricultural operations. This backend provides a scalable API to handle complex product flows, kardex movements, and a granular role-based access control (RBAC) system.

---

## 🛠️ Tech Stack

- **Language**: Java 25 (LTS)
- **Framework**: Spring Boot 3.5.12
- **Persistence**: Spring Data JPA / Hibernate
- **Database**: PostgreSQL 15.14
- **Documentation**: OpenAPI 3 / Swagger UI
- **Mapping**: MapStruct
- **Build Tool**: Maven

---

## 🏗️ Core Modules

### 🔐 Authentication & Security
- **JWT-based Auth**: Secure stateless authentication.
- **Dynamic Context**: Multi-tenant support for different companies.
- **RBAC**: Granular permission system (Create, Read, Update, Delete) managed by roles.

### 📦 Inventory & Kardex
- **Kardex Movements**: Full traceability of product entries, exits, and transfers.
- **Auditing 2.0**: Advanced tracking of every movement using `Instant` timestamps and user-linked auditing.
- **Product Management**: Handling of product presentations, batches (lotes), and expiration dates.

### 👥 User Management
- **User Lifecycle**: Registration, role assignment, and status management (Active/Inactive).
- **Persona Integration**: Atomic linkage between system users and personal identity data.

---

## 🚀 Getting Started

### Prerequisites
- JDK 25
- Maven 3.9+
- PostgreSQL 15+

### Installation & Execution

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd coagronet-backend
   ```

2. **Configure Environment**:
   Create or edit the `.env` file in the root directory with your database credentials.

3. **Run the application**:
   ```bash
   mvn clean spring-boot:run
   ```

### Useful Commands
| Command | Description |
| :--- | :--- |
| `mvn clean spring-boot:run` | Runs the application in local profile |
| `mvn clean verify` | Formats, compiles, tests and packages the project |
| `mvn spring-javaformat:apply` | Applies project-wide code formatting |
| `mvn -Pdeploy clean package` | Generates the production WAR file |

---

## 📖 API Documentation

Once the server is running, you can access the interactive API documentation via Swagger UI:

👉 **`http://localhost:8080/swagger-ui.html`**

---

© 2026 Coagronet Project Team.
