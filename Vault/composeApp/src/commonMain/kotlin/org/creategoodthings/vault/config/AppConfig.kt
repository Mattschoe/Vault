package org.creategoodthings.vault.config

object AppConfig {
    const val IS_DEV = true //TODO CHANGE BEFORE FINAL BUILD
    const val AUTH_REFRESH_ENDPOINT = "/api/collections/users/auth-refresh"
    const val AUTH_WITH_PASSWORD_ENDPOINT = "/api/collections/users/auth-with-password"
    const val USERS_ENDPOINT = "/api/collections/users/records"
    const val PRODUCTS_ENDPOINT = "/api/collections/product/records"
    const val STORAGES_ENDPOINT = "/api/collections/storage/records"
    const val CONTAINERS_ENDPOINT = "/api/collections/container/records"
    const val INVITATIONS_ENDPOINT = "/api/collections/invitations/records"

    val BASE_URL: String
        get() = if (IS_DEV) "http://10.0.2.2:8090" //Android Platform to reach host IP
                else "https://api.creategoodthings.dk/vault" //TODO Is this the right endpoint?
}