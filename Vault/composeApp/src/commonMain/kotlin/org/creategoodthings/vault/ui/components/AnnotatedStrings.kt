package org.creategoodthings.vault.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import org.jetbrains.compose.resources.stringResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.dont_have_account
import vault.composeapp.generated.resources.sign_up

/**
 * A annotated text with a link
 * @param prefixText the text before the link
 * @param linkText the text acting as a link
 * @param onSignUpClick called when user interacts with [linkText]
 */
@Composable
fun TextWithLink(
    prefixText: String,
    linkText: String,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val annotatedText = buildAnnotatedString {
        append(prefixText)
        append(" ")
        val link = LinkAnnotation.Clickable(
            tag = "sign_up_action",
            linkInteractionListener = { onSignUpClick() }
        )
        withLink(link) {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                append(linkText)
            }
        }
    }

    Text(
        text = annotatedText,
        modifier = modifier
    )
}