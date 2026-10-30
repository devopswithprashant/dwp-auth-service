package com.devopswithprashant.service.auth.controller

import com.devopswithprashant.service.auth.dto.request.LoginRequest
import com.devopswithprashant.service.auth.dto.request.RegisterRequest
import com.devopswithprashant.service.auth.entity.Role
import com.devopswithprashant.service.auth.entity.RoleType
import com.devopswithprashant.service.auth.repository.RoleRepository
import com.devopswithprashant.service.auth.repository.UserCredentialRepository
import com.devopswithprashant.service.auth.repository.UserProfileProvisioningRepository
import com.devopswithprashant.service.auth.service.UserProfileProvisioningService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import com.fasterxml.jackson.databind.ObjectMapper
import java.time.Duration
import java.time.Instant

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userCredentialRepository: UserCredentialRepository

    @Autowired
    private lateinit var userProfileProvisioningRepository: UserProfileProvisioningRepository

    @Autowired
    private lateinit var userProfileProvisioningService: UserProfileProvisioningService

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setup() {
        jdbcTemplate.execute("TRUNCATE TABLE credential_roles, user_credentials, roles RESTART IDENTITY CASCADE")

        roleRepository.save(Role(name = RoleType.ROLE_USER))
        roleRepository.save(Role(name = RoleType.ROLE_ADMIN))
    }

    @Test
    fun `register and login endpoints work end to end`() {
        val registerRequest = RegisterRequest(
            username = "jane",
            email = "jane@example.com",
            password = "Password123!"
        )

        val registrationResult = mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data.username").value("jane"))
            .andExpect(jsonPath("$.data.profileProvisioningStatus").value("PENDING"))
            .andReturn()

        val registeredId = objectMapper.readTree(registrationResult.response.contentAsString)
            .path("data")
            .path("id")
            .asText()
        val provisioningJob = userProfileProvisioningRepository.findAll().single()
        assertEquals(registeredId, provisioningJob.userId.toString())
        assertEquals("PENDING", provisioningJob.status.name)

        val claimedJob = userProfileProvisioningService.claimNext(
            Instant.now(),
            Duration.ofSeconds(10)
        )
        assertEquals(provisioningJob.userId, claimedJob?.userId)
        assertEquals(1, claimedJob?.attemptCount)

        val loginRequest = LoginRequest(
            identifier = "jane",
            password = "Password123!"
        )

        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.token.accessToken").exists())
    }

    @Test
    fun `me endpoint returns authenticated user details`() {
        val user = userCredentialRepository.save(
            com.devopswithprashant.service.auth.entity.UserCredential(
                username = "john",
                email = "john@example.com",
                passwordHash = passwordEncoder.encode("Password123!")
            ).apply {
                assignRole(roleRepository.findByName(RoleType.ROLE_USER)!!)
            }
        )

        val loginResponse = mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(LoginRequest(identifier = "john", password = "Password123!")))
        )
            .andExpect(status().isOk)
            .andReturn()

        val token = objectMapper.readTree(loginResponse.response.contentAsString)
            .path("data")
            .path("token")
            .path("accessToken")
            .asText()

        mockMvc.perform(
            get("/api/v1/auth/me")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.username").value("john"))
    }
}
