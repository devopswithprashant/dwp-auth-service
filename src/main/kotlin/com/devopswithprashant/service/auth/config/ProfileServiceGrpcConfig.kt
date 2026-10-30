package com.devopswithprashant.service.auth.config

import com.devopswithprashant.profile.v1.ProfileServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProfileServiceGrpcConfig {

    @Bean(destroyMethod = "shutdown")
    fun profileServiceChannel(properties: ProfileServiceProperties): ManagedChannel =
        ManagedChannelBuilder.forTarget(properties.grpcTarget)
            .usePlaintext()
            .build()

    @Bean
    fun profileServiceBlockingStub(
        channel: ManagedChannel
    ): ProfileServiceGrpc.ProfileServiceBlockingStub =
        ProfileServiceGrpc.newBlockingStub(channel)
}
