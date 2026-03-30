package org.creategoodthings.vault.domain

import kotlin.jvm.JvmInline
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Container (
    val ID: ContainerID,
    val storageID: String,
    val name: String
)

@JvmInline
value class ContainerID(val value: String) {
    override fun toString(): String = value
    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun generate(): ContainerID = ContainerID(Uuid.random().toString())
    }
}