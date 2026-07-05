# Auth Service Architecture

```
React UI
    |
API Gateway (future)
    |
Auth Service
    |
PostgreSQL
```

Authentication Flow

Register -> Store User -> BCrypt Password -> ROLE_USER

Login -> Validate Credentials -> Generate JWT

Protected API
Client -> JWT Filter -> Validate Token -> Load User -> SecurityContext -> Controller
