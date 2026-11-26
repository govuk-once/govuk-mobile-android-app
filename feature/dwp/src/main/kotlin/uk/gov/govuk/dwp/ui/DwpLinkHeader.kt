package uk.gov.govuk.dwp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import java.time.LocalDate
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.dwp.DwpLinkWidgetViewModel
import uk.gov.govuk.dwp.ui.component.DwpLinkCard
import uk.gov.govuk.dwp.util.benefitFrequencyContentItem
import uk.gov.govuk.dwp.util.latestUpcomingPayments

@Composable
fun DwpLinkHeader(
    onActionClick: () -> Unit,
    onShowTopicContent: (Boolean) -> Unit = {},
    onClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: DwpLinkWidgetViewModel = hiltViewModel()
    val state by viewModel.dwpState.collectAsState(initial = ServiceLinkStatus.CHECKING)
    DwpLinkHeaderContent(
        state = state,
        onActionClick = {
            onActionClick()
        },
        onShowTopicContent = onShowTopicContent,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
internal fun DwpLinkHeaderContent(
    state: ServiceLinkStatus,
    onActionClick: (String) -> Unit,
    onShowTopicContent: (Boolean) -> Unit = {},
    onClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLinked = state == ServiceLinkStatus.LINKED
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(isLinked, selectedTab) {
        onShowTopicContent(!isLinked || selectedTab == 1)
    }

    Column(modifier = modifier) {
        when (state) {
            ServiceLinkStatus.LINKED -> {
                BenefitsToggleSwitch(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )

                if (selectedTab == 0) {
                    BenefitsPaymentsContent(onClick = onClick)
                }
            }

            else -> {
                DwpLinkCard(state = state, onActionClick = onActionClick)
                SmallVerticalSpacer()
            }
        }
    }
}

@Composable
private fun BenefitsPaymentsContent(
    onClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val latestUpcomingPayments = latestUpcomingPayments().data.upcoming
    val benefitFrequency = benefitFrequencyContentItem()

    Column(modifier = modifier) {
        for (payment in latestUpcomingPayments.data) {
            Row(modifier = Modifier.fillMaxWidth()) {
                val benefitName = payment.amountsPerBenefitCode.first().benefitName
                val frequency = benefitFrequency[benefitName] ?: ""
                val benefitCode = payment.amountsPerBenefitCode.first().benefitShortName

                PaymentCard(
                    benefitName = benefitName,
                    frequency = frequency,
                    amount = payment.amount,
                    dueDate = LocalDate.parse(payment.dueDate),
                    benefitCode = benefitCode,
                    onClick = onClick,
                )
            }
            Spacer(Modifier.height(24.dp))
        }

        UniversalCreditCard()
        Spacer(Modifier.height(24.dp))
    }
}
