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
- Durable asynchronous User Service profile provisioning

## Technology Stack
- Kotlin
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- JWT (HS384)

## Run

Set `JWT_SECRET` to a value containing at least 48 UTF-8 bytes before starting
the service.

```bash
./gradlew bootRun
```

## Default APIs

- POST /api/v1/auth/register
- POST /api/v1/auth/login
- GET /api/v1/auth/me

Registration commits an Auth identity and a pending profile-provisioning job
together. A background worker provisions the profile over gRPC and retries
transient errors with bounded backoff. See
[`docs/architecture/auth-service.md`](docs/architecture/auth-service.md) for
deployment configuration and recovery behavior.

## Future Roadmap
- Refresh Tokens
- Logout
- Email Verification
- Forgot Password
