# PersonApp - Hexagonal Architecture with Spring Boot

This repository showcases a **Hexagonal (Clean) Architecture** implementation using **Spring Boot**. It handles `Person` entities and related records such as `Phone` numbers and `Studies`, with persistence options for both **MariaDB** and **MongoDB**.

---

## 🚀 Key Features

- **Hexagonal Architecture**: Business logic is isolated from infrastructure like databases and interfaces.
- **Spring Boot**: Simplifies setup, configuration, and deployment.
- **Multi-Module Maven Project**:
  - `domain`: Core models and business rules.
  - `application`: Use cases and input/output port definitions.
  - `common`: Shared utilities and exceptions.
  - `rest-input-adapter`: RESTful API implementation.
  - `cli-input-adapter`: Terminal-based user interface.
  - `maria-output-adapter`: JPA implementation for MariaDB.
  - `mongo-output-adapter`: Spring Data MongoDB implementation.
- **Dockerized**: Dockerfiles and a `docker-compose.yml` for containerized setup.
- **API Docs with Swagger**: Easily test and explore endpoints.
- **115+ CLI Unit Tests**: High coverage for command-line behavior.

---

## 🧰 Prerequisites

- Java 11+
- Apache Maven
- Docker
- Docker Compose

---

## 🛠️ Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/Gaoux/personapp-hexa-spring-boot
cd personapp-hexa-spring-boot-main
```

### 2. Build All Modules

```bash
mvn clean install -DskipTests
```

This compiles and packages the entire application.

---

## 🐳 Running with Docker

Start all backend services and the REST API using Docker:

```bash
docker-compose up --build -d
```

This command launches:

- `personapp-api` on `http://localhost:3000`
- `personapp-mariadb` on `localhost:3307`
- `personapp-mongodb` on `localhost:27017`

If it's the first time, schema and data initialization scripts will be applied automatically.

---

## 🌐 Accessing the REST API

- Swagger UI:  
  [http://localhost:3000/swagger-ui/index.html](http://localhost:3000/swagger-ui/index.html)
- OpenAPI JSON:  
  [http://localhost:3000/api-docs](http://localhost:3000/api-docs)

---

## 🖥️ Running the CLI (Manual Setup)

You can run the CLI locally while the backend runs in Docker.

### Step-by-step:

1. **Ensure backend is running**:

   ```bash
   docker-compose up -d
   ```

2. **Build the CLI JAR** (if not already built):

   ```bash
   mvn clean install -pl cli-input-adapter -am -DskipTests
   ```

3. **Run the CLI application**:
   ```bash
   java -jar cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar
   ```

---

## 🔁 Using Both Interfaces

You can interact with the app via:

- The browser-based REST API
- The terminal-based CLI

Both interfaces connect to the same database backend and reflect real-time updates.

**Database Configuration:**

- **MariaDB JDBC URL:**  
  `jdbc:mariadb://localhost:3307/persona_db`
- **MongoDB URI:**  
  `mongodb://persona_db:persona_db@localhost:27017/persona_db?authSource=admin`

---

## 🧪 Running Unit Tests

The CLI module has full test coverage. Run the tests with:

```bash
mvn test -pl cli-input-adapter
```

Test breakdown:

- `PersonaInputAdapterCliTest`: 29 tests
- `ProfesionInputAdapterCliTest`: 19 tests
- `StudyInputAdapterCliTest`: 40 tests
- `TelefonoInputAdapterCliTest`: 27 tests

---

## 🛑 Stopping Containers

Stop the running Docker services:

```bash
docker-compose down
```

To also remove all volumes (deletes data):

```bash
docker-compose down -v
```

---

## 🧱 Project Structure

```
personapp-hexa-spring-boot/
├── common/
├── domain/
├── application/
│   ├── port/in/
│   ├── port/out/
│   └── usecase/
├── rest-input-adapter/
├── cli-input-adapter/
├── maria-output-adapter/
├── mongo-output-adapter/
├── scripts/
├── Dockerfile
├── mariadb.Dockerfile
├── mongodb.Dockerfile
├── docker-compose.yml
├── docker-application.properties
```
---

## 📄 License

Licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for more details.
