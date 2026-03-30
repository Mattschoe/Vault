package org.creategoodthings.vault.domain

import kotlin.jvm.JvmInline
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Storage @OptIn(ExperimentalUuidApi::class) constructor(
    val ID: StorageID,
    val name: String
)

@JvmInline
value class StorageID(val value: String) {
    override fun toString(): String = value
    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun generate(): StorageID = StorageID(Uuid.random().toString())
    }
}