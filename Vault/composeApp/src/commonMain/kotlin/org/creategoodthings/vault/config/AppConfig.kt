package org.creategoodthings.vault.config

object AppConfig {
    private const val IS_DEV = true //TODO CHANGE BEFORE FINAL BUILD

    val BASE_URL: String
        get() = if (IS_DEV) "http://10.0.2.2:8090" //Android Platform to reach host IP
                else "https://api.creategoodthings.dk/vault" //TODO Is this the right endpoint?
}