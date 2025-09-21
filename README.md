#medicache
A Spring Boot application for managing and retrieving patient data with efficient caching mechanisms.

## Features

- Patient data management with visits, diagnoses, and doctors
- Multi-level caching (Hibernate L2 cache + Spring Cache with Caffeine)
- Efficient pagination for large datasets
- Docker containerization
- Jenkins CI/CD pipeline integration
- RESTful API endpoints

## Technologies

- Java 17
- Spring Boot 3.1.0
- Hibernate with JPA
- PostgreSQL
- Caffeine Cache
- Docker
- Jenkins

## Quick Start

### Prerequisites
- Java 17+
- Docker and Docker Compose
- Gradle

### Running with Docker

1. Build and start the application:
```bash
docker-compose up --build
