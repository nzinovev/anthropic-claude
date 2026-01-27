# Anthropic Claude Demo Project

This is a Spring Boot REST API application that demonstrates a complete implementation developed using **Anthropic Claude 3.5 Sonnet**.

## Project Overview

This application showcases a fully functional REST API with:
- Spring Boot 3.4.1 framework
- PostgreSQL database integration
- Data validation and JPA support
- CRUD operations with proper testing
- Database migrations with Liquibase

## Technology Stack

- **Java 21**
- **Spring Boot 3.4.1**
- **PostgreSQL**
- **Maven**
- **Liquibase** for database migrations
- **JUnit** for testing

## Development Details

This entire project was built using **Anthropic Claude 3.5 Sonnet**, an advanced AI assistant. For detailed information about the implementation approach and technical decisions, refer to the following article:

📖 [Implementation Article](https://habr.com/ru/articles/886960/) *(Note: This article is written in Russian)*

## Building and Running

### Prerequisites
- Java 21
- PostgreSQL
- Maven

### Build
```bash
./mvnw clean install
```

### Run
```bash
./mvnw spring-boot:run
```

## Project Structure

- `src/main/java` - Application source code
- `src/main/resources` - Configuration and database migrations
- `src/test` - Unit and integration tests
- `anthropic-claude-db` - Database configuration

---

Built with ✨ **Anthropic Claude 3.5 Sonnet**
