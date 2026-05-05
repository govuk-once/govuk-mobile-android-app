package uk.gov.govuk.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.InternalLinkListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.NonTappableCard
import uk.gov.govuk.design.ui.component.Title
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.model.InternalLinkListItemStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.settings.LinkedAccountsUiState
import uk.gov.govuk.settings.R
import uk.gov.govuk.settings.ui.model.LinkedAccountUiModel

@Composable
internal fun YourAccountsRoute(
    accounts: List<LinkedAccountUiModel>,
    accountsUiState: LinkedAccountsUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    YourAccountsScreen(
        accounts = accounts,
        accountsUiState = accountsUiState,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun YourAccountsScreen(
    accounts: List<LinkedAccountUiModel>,
    accountsUiState: LinkedAccountsUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    var accountToUnlink by remember { mutableStateOf<LinkedAccountUiModel?>(null) }

    Column(
        modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.screenBackground)
    ) {
        ChildPageHeader(
            dismissStyle = HeaderDismissStyle.Back(onBack)
        )

        when (accountsUiState) {
            LinkedAccountsUiState.Default -> {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = GovUkTheme.spacing.large)
                ) {
                    Title(
                        title = stringResource(R.string.your_accounts_title)
                    )

                    LargeVerticalSpacer()

                    Column(Modifier.padding(horizontal = GovUkTheme.spacing.medium)) {
                        if (accounts.isEmpty()) {
                            NonTappableCard(
                                body = stringResource(R.string.your_accounts_empty_state_body),
                            )
                        } else {
                            accounts.forEach { account ->
                                val displayTitle = stringResource(id = account.displayTitleRes)
                                InternalLinkListItem(
                                    title = displayTitle,
                                    onClick = { },
                                    style = InternalLinkListItemStyle.Button(
                                        icon = uk.gov.govuk.design.R.drawable.ic_cancel_round,
                                        altText = stringResource(
                                            R.string.your_accounts_remove_account_alt_text,
                                            displayTitle
                                        )
                                    ) {
                                        accountToUnlink = account
                                    }
                                )
                            }
                        }
                    }
                }

            }

            LinkedAccountsUiState.Unlinking -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = GovUkTheme.colourScheme.textAndIcons.primary
                    )
                }

            }

            LinkedAccountsUiState.Error -> {

            }
        }


        accountToUnlink?.let { account ->
            RemoveAccountDialog(
                displayTitle = stringResource(id = account.displayTitleRes),
                onConfirm = {
                    account.onUnlink()
                    accountToUnlink = null
                },
                onDismiss = {
                    accountToUnlink = null
                }
            )
        }
    }
}

@Composable
private fun RemoveAccountDialog(
    displayTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(GovUkTheme.numbers.cornerAndroidList),
        modifier = modifier,
        title = {
            BodyBoldLabel(
                text = "Remove $displayTitle?"
            )
        },
        text = {
            BodyRegularLabel(
                text = "You will not be able to see information from this account in the app, unless you add it again. You can still see it on the GOV.UK website.",
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                BodyBoldLabel(
                    text = "Remove account",
                    color = GovUkTheme.colourScheme.textAndIcons.buttonDestructive
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                BodyRegularLabel(
                    text = "Cancel",
                    color = GovUkTheme.colourScheme.textAndIcons.linkSecondary
                )
            }
        },
        containerColor = GovUkTheme.colourScheme.surfaces.alert
    )
}

@Preview(showBackground = true)
@Composable
private fun YourAccountsScreenEmptyPreview() {
    GovUkTheme {
        YourAccountsScreen(
            accounts = emptyList(),
            accountsUiState = LinkedAccountsUiState.Default,
            onBack = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun YourAccountsScreenPreview() {
    GovUkTheme {
        YourAccountsScreen(
            accounts = listOf(
                LinkedAccountUiModel(
                    displayTitleRes = R.string.manage_login_header_title,
                    onUnlink = {}
                )
            ),
            accountsUiState = LinkedAccountsUiState.Default,
            onBack = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun YourAccountsScreenUnlinkingPreview() {
    GovUkTheme {
        YourAccountsScreen(
            accounts = listOf(
                LinkedAccountUiModel(
                    displayTitleRes = R.string.manage_login_header_title,
                    onUnlink = {}
                )
            ),
            accountsUiState = LinkedAccountsUiState.Unlinking,
            onBack = { }
        )
    }
}