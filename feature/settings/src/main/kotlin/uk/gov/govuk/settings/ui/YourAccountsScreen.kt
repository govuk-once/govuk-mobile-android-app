package uk.gov.govuk.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.InternalLinkListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.NonTappableCard
import uk.gov.govuk.design.ui.component.Title
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.model.InternalLinkListItemStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.settings.R
import uk.gov.govuk.settings.YourAccountsViewModel
import uk.gov.govuk.settings.ui.model.LinkedAccountUiModel

@Composable
internal fun YourAccountsRoute(
    accounts: List<LinkedAccountUiModel>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: YourAccountsViewModel = hiltViewModel()

    YourAccountsScreen(
        accounts = accounts,
        onBack = onBack,
        onPageView = { viewModel.onPageView() },
        modifier = modifier
    )
}

@Composable
private fun YourAccountsScreen(
    accounts: List<LinkedAccountUiModel>,
    onBack: () -> Unit,
    onPageView: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(
        modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.screenBackground)
    ) {
        ChildPageHeader(
            dismissStyle = HeaderDismissStyle.Back(onBack)
        )

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
                        body = "Accounts you add to the app will appear here",
                    )
                } else {
                    accounts.forEach { account ->
                        val displayTitle = stringResource(id = account.displayTitleRes)
                        InternalLinkListItem(
                            title = displayTitle,
                            onClick = { },
                            style = InternalLinkListItemStyle.Button(
                                icon = uk.gov.govuk.design.R.drawable.ic_cancel_round,
                                altText = "Remove $displayTitle"
                            ) {
                                // TODO in future ticket: accountToUnlink = account
                                account.onUnlink()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun YourAccountsScreenEmptyPreview() {
    GovUkTheme {
        YourAccountsScreen(
            accounts = emptyList(),
            onBack = { },
            onPageView = { },
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
            onBack = { },
            onPageView = { },
        )
    }
}