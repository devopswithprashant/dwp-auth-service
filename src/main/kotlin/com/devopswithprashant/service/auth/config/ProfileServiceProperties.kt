package com.devopswithprashant.service.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "profile-service")
data class ProfileServiceProperties(
    var grpcTarget: String = "localhost:9090",
    var deadline: Duration = Duration.ofSeconds(3),
    var pollInterval: Duration = Duration.ofSeconds(1),
    var retryInitialBackoff: Duration = Duration.ofSeconds(5),
    var retryMaxBackoff: Duration = Duration.ofMinutes(15),
    var retryMaxAttempts: Int = 10,
    var leaseDuration: Duration = Duration.ofSeconds(10)
) {
    init {
        require(grpcTarget.isNotBlank()) { "profile-service.grpc-target must not be blank" }
        require(!deadline.isZero && !deadline.isNegative && deadline.toMillis() > 0) {
            "profile-service.deadline must be at least 1ms"
        }
        require(!pollInterval.isZero && !pollInterval.isNegative) {
            "profile-service.poll-interval must be positive"
        }
        require(!retryInitialBackoff.isZero && !retryInitialBackoff.isNegative) {
            "profile-service.retry-initial-backoff must be positive"
        }
        require(retryMaxBackoff >= retryInitialBackoff) {
            "profile-service.retry-max-backoff must be at least retry-initial-backoff"
        }
        require(retryMaxAttempts > 0) { "profile-service.retry-max-attempts must be positive" }
        require(leaseDuration > deadline) {
            "profile-service.lease-duration must exceed the gRPC deadline"
        }
    }
}
