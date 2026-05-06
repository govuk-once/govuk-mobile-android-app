package uk.gov.govuk.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.ErrorPage
import uk.gov.govuk.design.ui.component.InternalLinkListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.LoadingScreen
import uk.gov.govuk.design.ui.component.NonTappableCard
import uk.gov.govuk.design.ui.component.Title
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.model.InternalLinkListItemStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.settings.LinkedAccountsUiState
import uk.gov.govuk.settings.R
import uk.gov.govuk.settings.YourAccountsViewModel
import uk.gov.govuk.settings.ui.model.LinkedAccountUiModel

@Composable
internal fun YourAccountsRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: YourAccountsViewModel = hiltViewModel()
) {

    val accounts by viewModel.linkedAccounts.collectAsStateWithLifecycle()
    val accountsUiState by viewModel.accountsUiState.collectAsStateWithLifecycle()

    YourAccountsScreen(
        accounts = accounts,
        accountsUiState = accountsUiState,
        onBack = onBack,
        onRemoveIconClicked = { service -> viewModel.onRemoveIconClicked(service) },
        onUnlinkConfirmed = { service, buttonLabel ->
            viewModel.unlinkAccount(service, buttonLabel)
        },
        onUnlinkCancelled = { service, buttonLabel ->
            viewModel.onUnlinkCancelled(service, buttonLabel)
        },
        onErrorDismiss = { viewModel.resetError() },
        modifier = modifier
    )
}

@Composable
private fun YourAccountsScreen(
    accounts: List<LinkedAccountUiModel>,
    accountsUiState: LinkedAccountsUiState,
    onBack: () -> Unit,
    onRemoveIconClicked: (String) -> Unit,
    onUnlinkConfirmed: (String, String) -> Unit,
    onUnlinkCancelled: (String, String) -> Unit,
    onErrorDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {

    var accountToUnlink by remember { mutableStateOf<LinkedAccountUiModel?>(null) }

    when (accountsUiState) {
        LinkedAccountsUiState.Error -> {
            RemoveAccountErrorScreen(
                onDismiss = onErrorDismiss,
                modifier = modifier
            )
        }

        LinkedAccountsUiState.Default -> {
            AccountsContainer(onBack = onBack, modifier = modifier) {
                AccountsListContent(
                    accounts = accounts,
                    onRemoveClick = {
                        accountToUnlink = it
                        onRemoveIconClicked(it.serviceName)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        LinkedAccountsUiState.Unlinking -> {
            AccountsContainer(onBack = onBack, modifier = modifier) {
                LoadingScreen(
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    accountToUnlink?.let { account ->
        val displayTitle = stringResource(id = account.displayTitleRes)
        val titleText = stringResource(R.string.remove_account_dialog_title, displayTitle)
        val bodyText = stringResource(R.string.remove_account_dialog_body)
        val confirmText = stringResource(R.string.remove_account_dialog_confirm_button_text)
        val cancelText = stringResource(R.string.remove_account_dialog_cancel_button_text)

        RemoveAccountDialog(
            titleText = titleText,
            bodyText = bodyText,
            confirmButtonText = confirmText,
            cancelButtonText = cancelText,
            onConfirm = {
                onUnlinkConfirmed(account.serviceName, confirmText)
                accountToUnlink = null
            },
            onDismiss = {
                onUnlinkCancelled(account.serviceName, cancelText)
                accountToUnlink = null
            }
        )
    }
}

@Composable
private fun AccountsContainer(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.screenBackground)
    ) {
        ChildPageHeader(
            dismissStyle = HeaderDismissStyle.Back(onBack)
        )

        Title(
            title = stringResource(R.string.your_accounts_title)
        )

        content()
    }
}

@Composable
private fun AccountsListContent(
    accounts: List<LinkedAccountUiModel>,
    onRemoveClick: (LinkedAccountUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = GovUkTheme.spacing.large)
    ) {
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
                            onRemoveClick(account)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RemoveAccountDialog(
    titleText: String,
    bodyText: String,
    confirmButtonText: String,
    cancelButtonText: String,
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
                text = titleText
            )
        },
        text = {
            BodyRegularLabel(
                text = bodyText,
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                BodyBoldLabel(
                    text = confirmButtonText,
                    color = GovUkTheme.colourScheme.textAndIcons.buttonDestructive
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                BodyRegularLabel(
                    text = cancelButtonText,
                    color = GovUkTheme.colourScheme.textAndIcons.linkSecondary
                )
            }
        },
        containerColor = GovUkTheme.colourScheme.surfaces.alert
    )
}

@Composable
private fun RemoveAccountErrorScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorPage(
        headerText = "There’s a problem",
        subText = "We could not remove your driver and vehicles account. Try again later.",
        buttonText = "Go back to your accounts",
        onBack = { onDismiss() },
        modifier = modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.screenBackground)
    )
}

@Preview(showBackground = true)
@Composable
private fun YourAccountsScreenEmptyPreview() {
    GovUkTheme {
        YourAccountsScreen(
            accounts = emptyList(),
            accountsUiState = LinkedAccountsUiState.Default,
            onBack = { },
            onRemoveIconClicked = { },
            onErrorDismiss = { },
            onUnlinkConfirmed = { _, _ -> },
            onUnlinkCancelled = { _, _ -> }
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
                    serviceName = "dvla",
                    displayTitleRes = R.string.manage_login_header_title
                )
            ),
            accountsUiState = LinkedAccountsUiState.Default,
            onBack = { },
            onRemoveIconClicked = { },
            onErrorDismiss = { },
            onUnlinkConfirmed = { _, _ -> },
            onUnlinkCancelled = { _, _ -> }
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
                    serviceName = "dvla",
                    displayTitleRes = R.string.manage_login_header_title
                )
            ),
            accountsUiState = LinkedAccountsUiState.Unlinking,
            onBack = { },
            onRemoveIconClicked = { },
            onErrorDismiss = { },
            onUnlinkConfirmed = { _, _ -> },
            onUnlinkCancelled = { _, _ -> }
        )
    }
}