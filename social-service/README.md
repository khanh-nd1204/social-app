# 🚀 Social Service - Setup & Run Guide

## 📌 1. Prerequisites

Trước khi bắt đầu, hãy đảm bảo rằng bạn đã cài đặt các công cụ cần thiết:

- [JDK 17+](https://www.oracle.com/vn/java/technologies/downloads/)
- [Maven 3.5+](https://maven.apache.org/download.cgi)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)

---

## 🛠️ 2. Tech Stack

Dự án sử dụng các công nghệ sau:

- **Java 17**
- **Maven 3.5+**
- **Spring Boot 3.4.5**
- **Spring Data Validation**
- **Spring Data JPA**
- **MySQL**
- **Lombok**
- **Spring DevTools**
- **Spring Security**
- **WebSocket**
- **OpenAPI (Swagger)**
- **Docker**

---

## ▶️ 3. Build & Run Application

### 🔧 Chạy trực tiếp bằng Maven:

```
$ ./mvnw spring-boot:run
```

### 🐳 Chạy bằng Docker:

```
# Build project với profile 'dev'
$ mvn clean install -P dev

# Build Docker image
$ docker build -t social-service:latest .

# Chạy container
$ docker run -it -p 8090:8090 --name social-service social-service:latest
```
