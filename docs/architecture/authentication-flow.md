# Authentication Flow

1. User registers.
2. User logs in.
3. Backend returns JWT access token.
4. Frontend stores the token.
5. Every protected request includes:

Authorization: Bearer <token>

6. JwtAuthenticationFilter validates the token.
7. SecurityContext is populated.
8. Controller accesses the authenticated user.
