# Auth Service Architecture

```
App UI
    |
API Gateway (future)
    |
Auth Service
    |                         |
PostgreSQL              User Service (gRPC)
```

Authentication Flow

Register -> Store User + Pending Profile Job -> Commit -> CreateDefaultProfile

Login -> Validate Credentials -> Generate JWT

Protected API
Client -> JWT Filter -> Validate Token -> Load User -> SecurityContext -> Controller

## Profile provisioning

The Auth Service owns the identity and credentials; the User Service owns
profile data. Registration writes the Auth identity and a `PENDING`
`user_profile_provisioning` record in one Auth database transaction. A scheduled
worker claims jobs using a short database lease, commits the claim, and only
then makes the gRPC call, so no Auth transaction remains open during network
I/O. Both services use the original canonical Auth UUID.

`UNAVAILABLE` and `DEADLINE_EXCEEDED` are retried with persisted, bounded
exponential backoff. Other statuses, an unexpected response UUID, and jobs
exceeding the configured attempt limit become `FAILED`. A lost response is safe
to retry because `CreateDefaultProfile` is idempotent. Inspect
`user_profile_provisioning` for pending and failed work; failed records require
operational reconciliation before being returned to `PENDING`. Login and JWT
issuance continue to work while provisioning is pending; duplicate registration
continues to return the existing username/email conflict response.

After repairing the cause of a terminal failure, an operator can requeue that
same identity and UUID with:

```sql
UPDATE user_profile_provisioning
SET status = 'PENDING',
    attempt_count = 0,
    next_attempt_at = CURRENT_TIMESTAMP,
    lease_expires_at = NULL,
    last_status_code = NULL,
    updated_at = CURRENT_TIMESTAMP
WHERE user_id = '<canonical-auth-user-uuid>'
  AND status = 'FAILED';
```

Do not create a second Auth identity or manually alter profile data to recover
a job.

The gRPC channel uses plaintext and is intended only for a trusted internal
network, consistent with the current User Service server. Do not expose it to
untrusted networks. JWTs are not forwarded as gRPC authentication.

### Runtime configuration

The local profile defaults to `localhost:9090`. The `env` profile requires
these environment variables; configure the target and values for each
deployment rather than relying on local defaults:

| Variable | Purpose |
| --- | --- |
| `USER_PROFILE_GRPC_TARGET` | User Service gRPC target, e.g. `user-service:9090` |
| `USER_PROFILE_GRPC_DEADLINE` | Per-RPC deadline, e.g. `3s` |
| `USER_PROFILE_POLL_INTERVAL` | Durable queue poll interval, e.g. `1s` |
| `USER_PROFILE_RETRY_INITIAL_BACKOFF` | First retry delay, e.g. `5s` |
| `USER_PROFILE_RETRY_MAX_BACKOFF` | Backoff cap, e.g. `15m` |
| `USER_PROFILE_RETRY_MAX_ATTEMPTS` | Maximum RPC attempts |
| `USER_PROFILE_LEASE_DURATION` | Claim lease; must exceed the RPC deadline |
| `JWT_SECRET` | Shared Auth/User HS384 signing secret (at least 48 UTF-8 bytes) |
| `JWT_ISSUER` | Must be `devopswithprashant-auth` |
| `JWT_AUDIENCE` | Must be `devopswithprashant-platform` |
| `JWT_ACCESS_TOKEN_VALIDITY` | Token lifetime in seconds, e.g. `3600` |

No JWT signing secret is checked into this repository. Supply a secret of at
least 48 UTF-8 bytes through `JWT_SECRET` for local runs and from the platform's
secret manager in deployment.

The checked-in proto snapshot at
[`src/main/protobuf/user_profile.proto`](../../src/main/protobuf/user_profile.proto)
is mirrored from the canonical
[User Service contract](https://github.com/devopswithprashant/dwp-user-service/blob/main/src/main/protobuf/user_profile.proto).
Regenerate the client from this snapshot. When changing the contract, update
the snapshot from that canonical file and verify the RPC path remains
`/dwp.userservice.v1.UserProfileService/CreateDefaultProfile`.
