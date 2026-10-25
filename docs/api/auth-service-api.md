# Auth Service API

## Base URL
`http://localhost:8081`

## POST /api/v1/auth/register
Registers a new user.

Request:
```json
{
  "username":"prashant",
  "email":"prashant@example.com",
  "password":"Password@123"
}
```

Success: **201 Created**

The response includes `profileProvisioningStatus: "PENDING"`. The Auth identity
and a durable provisioning job are committed together; the Profile Service call
runs asynchronously and may complete after this response.

Errors:
- AUTH-001 Username already exists
- AUTH-002 Email already exists

Profile provisioning failures do not roll back a committed Auth identity.
Transient `UNAVAILABLE` and `DEADLINE_EXCEEDED` outcomes are retried with
bounded exponential backoff. Terminal or exhausted jobs are recorded as
`FAILED` in the Auth database for operational reconciliation.

---

## POST /api/v1/auth/login

Request:
```json
{
  "identifier":"prashant",
  "password":"Password@123"
}
```

Success: **200 OK**

Returns JWT access token.

---

## GET /api/v1/auth/me

Header:
`Authorization: Bearer <access_token>`

Returns authenticated user profile.

Success: **200 OK**

Unauthorized:
- Missing token -> 401
- Invalid token -> 401
