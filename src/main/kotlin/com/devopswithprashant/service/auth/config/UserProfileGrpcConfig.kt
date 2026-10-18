package com.devopswithprashant.service.auth.config

import com.devopswithprashant.user.v1.UserProfileServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserProfileGrpcConfig {

    @Bean(destroyMethod = "shutdown")
    fun userProfileChannel(properties: UserProfileProperties): ManagedChannel =
        ManagedChannelBuilder.forTarget(properties.grpcTarget)
            .usePlaintext()
            .build()

    @Bean
    fun userProfileServiceBlockingStub(
        channel: ManagedChannel
    ): UserProfileServiceGrpc.UserProfileServiceBlockingStub =
        UserProfileServiceGrpc.newBlockingStub(channel)
}
