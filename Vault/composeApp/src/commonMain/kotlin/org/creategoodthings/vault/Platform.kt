package org.creategoodthings.vault

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform