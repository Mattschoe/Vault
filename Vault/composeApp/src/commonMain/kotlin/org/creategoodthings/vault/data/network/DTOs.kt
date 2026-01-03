package org.creategoodthings.vault.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDTO(
    @SerialName("token") val token: String,
    @SerialName("record") val record: UserDTO
)

@Serializable
data class LoginRequestDTO(
    @SerialName("identity") val identity: String,
    @SerialName("password") val password: String
)

@Serializable
data class RegisterRequestDTO(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("passwordConfirm") val passwordConfirm: String,
    @SerialName("name") val username: String
)

@Serializable
data class UserDTO(
    @SerialName("id") val ID: String,
    @SerialName("email") val email: String,
    @SerialName("is_premium") val isPremium: Boolean = false,
    @SerialName("username") val username: String? = null,
    @SerialName("created") val created: String? = null
)