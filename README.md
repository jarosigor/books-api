# Books API

A Spring Boot REST API for searching books and managing reviews, integrating with the Gutendex API and PostgreSQL database.

## 🔧 Technical Requirements

### Required Software

- **Java**: JDK 23 or higher
- **Maven**: 3.8+
- **Docker**: 20.10+ and Docker Compose 2.0+
- **Git**: For cloning the repository

### Dependencies

The application uses:
- **Spring Boot**: 3.4.1
- **PostgreSQL**: 16 (via Docker)
- **Liquibase**: Database migration management
- **Testcontainers**: For integration testing
- **OpenAPI Generator**: For API specification-first development

## ✨ Features

- 🔍 Search books by title from Gutendex API
- 📖 Get detailed book information
- ⭐ Create book reviews
- 📊 Automatic rating calculations
- 🐳 Dockerized deployment
- 🧪 Comprehensive test coverage (unit + integration)
- ⚙️ Caching and pagination support

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone git@github.com:jarosigor/books-api.git
cd bookApi
```

### 2. Configure Application Properties
The default configuration is in src/main/resources/application.properties

## 🐳 Running the Application
```bash
mvn clean package -U -DskipTests
docker-compose up --build
```

## 🧪 Running Tests
Verify Docker is running and execute:
```bash
docker ps
mvn test
```

## 📄 API Documentation
Api documentation is available at: src/main/resources/specs/book-api.yaml