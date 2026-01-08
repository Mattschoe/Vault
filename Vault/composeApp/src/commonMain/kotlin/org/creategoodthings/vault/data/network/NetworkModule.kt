package org.creategoodthings.vault.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.logging.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.serialization.json.Json
import org.creategoodthings.vault.config.AppConfig
import org.creategoodthings.vault.domain.repositories.PreferencesRepository

fun createHttpClient(
    prefRepo: PreferencesRepository
): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 15_000
        }

        if (AppConfig.IS_DEV) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val token = prefRepo.token.first()
                    if (!token.isNullOrBlank()) BearerTokens(accessToken = token, refreshToken = "")
                    else null
                }
            }
        }

        defaultRequest {
            url(AppConfig.BASE_URL)
        }
        expectSuccess = true
    }
}