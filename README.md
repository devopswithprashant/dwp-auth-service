# Auth Service

## Overview
Spring Boot + Kotlin authentication service for the DevOps with Prashant platform.

## Features
- User registration
- Login using username or email
- BCrypt password hashing
- JWT access tokens
- Role-based authentication
- Protected endpoints
- Flyway migrations

## Technology Stack
- Kotlin
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- JWT (HS384)

## Run

```bash
./gradlew bootRun
```

## Default APIs

- POST /api/v1/auth/register
- POST /api/v1/auth/login
- GET /api/v1/auth/me

## Future Roadmap
- Refresh Tokens
- Logout
- Email Verification
- Forgot Password
