package org.creategoodthings.vault.domain

import kotlinx.datetime.LocalDate
import org.creategoodthings.vault.ui.RemindMeType
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
/**
 * A SuggestedProduct is a product that is suggested to the user.
 * It contains values that doesn't hold any truth and are arbitrary,
 * but helps the users initial journey in the app.
 * @param suggestedBestBefore what is the suggested best before of this product?
 * @param suggestedReminderType the type of reminder length
 * @param suggestedReminderAmount how many units of [suggestedReminderType]
 */
data class SuggestedProduct(
    val nameID: StringResource,
    val suggestedBestBefore: LocalDate,
    val suggestedReminderType: RemindMeType,
    val suggestedReminderAmount: Int,
    val ID: String = Uuid.random().toString()
)