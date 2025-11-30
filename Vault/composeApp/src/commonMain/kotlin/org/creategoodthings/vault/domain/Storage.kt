package org.creategoodthings.vault.domain

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Storage @OptIn(ExperimentalUuidApi::class) constructor(
    val ID: String = Uuid.random().toString(),
    val name: String
)