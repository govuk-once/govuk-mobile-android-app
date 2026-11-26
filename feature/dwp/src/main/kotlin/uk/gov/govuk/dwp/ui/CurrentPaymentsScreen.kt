package uk.gov.govuk.dwp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ListDivider
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.component.Title3BoldLabel
import uk.gov.govuk.design.ui.component.Title3RegularLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dwp.R
import uk.gov.govuk.dwp.util.benefitFrequencyContentItem
import uk.gov.govuk.dwp.util.convert
import uk.gov.govuk.dwp.util.latestUpcomingPayments
import uk.gov.govuk.dwp.util.toDistanceInWordsFormat

@Composable
internal fun CurrentPaymentsRoute(
    onClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    CurrentPaymentsScreen(
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
internal fun CurrentPaymentsScreen(
    onClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.chatBackground)
            .padding(top = GovUkTheme.spacing.medium)
    ) {
        Column(
            Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = GovUkTheme.spacing.medium)
            ) {
                item {
                    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
                    BenefitsToggleSwitch(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }

                val latestUpcomingPayments = latestUpcomingPayments().data.upcoming
                val benefitFrequency = benefitFrequencyContentItem()

                for (payment in latestUpcomingPayments.data) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            val benefitName = payment.amountsPerBenefitCode.first().benefitName
                            val frequency = benefitFrequency[benefitName] ?: ""

                            PaymentCard(
                                benefitName = benefitName,
                                frequency = frequency,
                                amount = payment.amount,
                                dueDate = LocalDate.parse(payment.dueDate), // Format: YYYY-MM-DD
                                benefitCode = payment.amountsPerBenefitCode.first().benefitShortName,
                                onClick = onClick
                            )
                        }
                    }
                    item {
                        Spacer(Modifier.height(24.dp))
                    }
                }

                item {
                    UniversalCreditCard()
                }

                item {
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
internal fun BenefitsToggleSwitch(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(45.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = GovUkTheme.spacing.medium)
            .background(Color(0xFFDDDDDD), shape)
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .then(
                    if (selectedTab == 0) {
                        Modifier.background(Color(0xFFFFFFFF), RoundedCornerShape(43.dp))
                    } else {
                        Modifier
                    }
                )
                .clip(RoundedCornerShape(43.dp))
                .clickable { onTabSelected(0) }
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.dwp_payments_your_benefits),
                style = if (selectedTab == 0) GovUkTheme.typography.captionBold else GovUkTheme.typography.captionRegular,
                color = if (selectedTab ==
                    0
                ) {
                    GovUkTheme.colourScheme.textAndIcons.primary
                } else {
                    GovUkTheme.colourScheme.textAndIcons.secondary
                },
                textAlign = TextAlign.Center
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .then(
                    if (selectedTab == 1) {
                        Modifier.background(Color(0xFFFFFFFF), RoundedCornerShape(43.dp))
                    } else {
                        Modifier
                    }
                )
                .clip(RoundedCornerShape(43.dp))
                .clickable { onTabSelected(1) }
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.dwp_payments_other_benefits),
                style = if (selectedTab == 1) GovUkTheme.typography.captionBold else GovUkTheme.typography.captionRegular,
                color = if (selectedTab ==
                    1
                ) {
                    GovUkTheme.colourScheme.textAndIcons.primary
                } else {
                    GovUkTheme.colourScheme.textAndIcons.secondary
                },
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
internal fun PaymentCard(
    benefitName: String,
    frequency: String,
    amount: Double,
    dueDate: LocalDate,
    benefitCode: String,
    onClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.cardDefault
        )
    ) {
        PaymentHeader(text = benefitName)
        PaymentFrequency(text = frequency)
        PaymentAmount(amount = amount)
        PaymentDate(dueDate = dueDate)
        PaymentHistory(
            onClick = onClick,
            benefitCode = benefitCode,
            benefitName = benefitName,
            frequency = frequency
        )
    }
}

@Composable
private fun PaymentHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth()
            .padding(start = GovUkTheme.spacing.medium, end = GovUkTheme.spacing.medium, top = GovUkTheme.spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Title1BoldLabel(
            text = text,
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = GovUkTheme.colourScheme.surfaces.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { expanded = true },
                enabled = true,
                colors = IconButtonColors(
                    containerColor = GovUkTheme.colourScheme.surfaces.primary,
                    contentColor = GovUkTheme.colourScheme.textAndIcons.primaryInverse,
                    disabledContainerColor = GovUkTheme.colourScheme.surfaces.primary,
                    disabledContentColor = GovUkTheme.colourScheme.textAndIcons.primaryInverse
                ),
                modifier = modifier
                    .clip(RoundedCornerShape(60.dp))
                    .height(36.dp)
                    .width(36.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_more_horiz_24),
                    contentDescription = "",
                    modifier = Modifier.padding(all = GovUkTheme.spacing.small)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = 0.dp, y = -0.dp),
                shape = RoundedCornerShape(34.dp),
                containerColor = Color(0xFFF5F5F5),
                modifier = modifier
                    .padding(GovUkTheme.spacing.medium)
                    .width(250.dp)
            ) {
                MenuItem(
                    onClick = {
                        expanded = false
                    },
                    buttonText = stringResource(R.string.dwp_payments_menu_info_not_avail),
                    modifier = Modifier
                )

                MenuItem(
                    onClick = {
                        expanded = false
                    },
                    buttonText = stringResource(R.string.dwp_payments_menu_benefit_not_shown),
                    modifier = Modifier
                )

                MenuItem(
                    onClick = {
                        expanded = false
                    },
                    buttonText = stringResource(R.string.dwp_payments_menu_chat),
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun MenuItem(
    onClick: () -> Unit,
    buttonText: String,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        text = {
            Text(
                text = buttonText,
                style = GovUkTheme.typography.bodyRegular
            )
        },
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun PaymentFrequency(
    text: String,
    modifier: Modifier = Modifier
) {
    Title3RegularLabel(
        text = text,
        color = GovUkTheme.colourScheme.textAndIcons.secondary,
        modifier = modifier.padding(
            horizontal = GovUkTheme.spacing.medium
        )
    )
}

@Composable
private fun PaymentAmount(
    amount: Double,
    modifier: Modifier = Modifier
) {
    Title3BoldLabel(
        text = "£${amount.convert()}",
        color = GovUkTheme.colourScheme.textAndIcons.primary,
        modifier = modifier.padding(start = GovUkTheme.spacing.medium, end = GovUkTheme.spacing.medium, top = GovUkTheme.spacing.large)
    )
}

@Composable
private fun PaymentDate(
    dueDate: LocalDate,
    modifier: Modifier = Modifier
) {
    BodyRegularLabel(
        text = LocalDate.now().toDistanceInWordsFormat(dueDate),
        color = GovUkTheme.colourScheme.textAndIcons.secondary,
        modifier = modifier.padding(start = GovUkTheme.spacing.medium, end = GovUkTheme.spacing.medium, bottom = 12.dp)
    )
}

@Composable
private fun PaymentHistory(
    benefitCode: String,
    benefitName: String,
    frequency: String,
    onClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    ListDivider(
        modifier = modifier.fillMaxWidth().padding(horizontal = GovUkTheme.spacing.medium)
    )

    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(
                start = GovUkTheme.spacing.medium,
                end = GovUkTheme.spacing.medium,
                top = 12.dp,
                bottom = 12.dp
            )
            .clickable(
                onClick = {
                    onClick(benefitCode, benefitName, frequency)
                }
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodyRegularLabel(
            text = stringResource(R.string.dwp_payments_payment_history),
            color = GovUkTheme.colourScheme.textAndIcons.secondary,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_keyboard_arrow_right_24),
                contentDescription = "",
                tint = GovUkTheme.colourScheme.surfaces.primary
            )
        }
    }
}

@Composable
internal fun UniversalCreditCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(GovUkTheme.numbers.cornerAndroidList),
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.cardDefault
        )
    ) {
        Column(
            modifier = Modifier.padding(GovUkTheme.spacing.medium)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.dwp_payments_universal_credit_sign_in),
                    modifier = Modifier.weight(1f),
                    color = GovUkTheme.colourScheme.textAndIcons.link
                )

                Icon(
                    painter = painterResource(
                        uk.gov.govuk.design.R.drawable.ic_external_link
                    ),
                    contentDescription = stringResource(
                        uk.gov.govuk.design.R.string.opens_in_web_browser
                    ),
                    tint = GovUkTheme.colourScheme.textAndIcons.link,
                    modifier = Modifier.padding(start = GovUkTheme.spacing.large)
                )
            }

            SmallVerticalSpacer()

            BodyRegularLabel(
                text = stringResource(R.string.dwp_payments_universal_credit_info),
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        }
    }
}

@PreviewLightDark
@Composable
internal fun CurrentPaymentsScreenPreview() {
    GovUkTheme {
        CurrentPaymentsScreen(
            onClick = { _, _, _ -> },
            modifier = Modifier.background(GovUkTheme.colourScheme.surfaces.background)
        )
    }
}
