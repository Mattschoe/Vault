package org.creategoodthings.vault.domain

data class User(
    val ID: String,
    val email: String,
    val token: String,
    val isPremium: Boolean
)