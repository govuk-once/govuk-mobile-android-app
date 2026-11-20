package uk.gov.govuk.dwp.ui

import android.icu.text.DecimalFormat
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ListDivider
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.component.Title3BoldLabel
import uk.gov.govuk.design.ui.component.Title3RegularLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dwp.R
import uk.gov.govuk.dwp.data.remote.model.AwardDetailsResponse

@Composable
internal fun CurrentPaymentsScreen(
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
//                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = GovUkTheme.spacing.medium)
            ) {
                item {
                    Title1BoldLabel(
                        text = "Benefits",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = GovUkTheme.spacing.medium)
                    )
                }

                item {
                    ToggleSwitch()
                }

                val latestUpcomingPayments = latestUpcomingPayments().data.upcoming

                for (payment in latestUpcomingPayments.data) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            PaymentCard(
                                heading = payment.amountsPerBenefitCode.first().benefitName,
                                frequency = "Paid every 4 weeks", // TODO: Where does this come from?
                                amount = payment.amount,
                                translatedPaymentDate = payment.translatedDueDate
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
private fun ToggleSwitch(
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(45.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = GovUkTheme.spacing.medium)
            .background(Color(0xFFDDDDDD), shape)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(GovUkTheme.colourScheme.surfaces.primary, RoundedCornerShape(43.dp))
                .clip(RoundedCornerShape(43.dp))
                .clickable { }
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Your benefits",
                style = GovUkTheme.typography.captionBold,
                color = GovUkTheme.colourScheme.textAndIcons.primaryInverse,
                textAlign = TextAlign.Center
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(43.dp))
                .clickable { }
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Other benefits",
                style = GovUkTheme.typography.captionRegular,
                color = GovUkTheme.colourScheme.textAndIcons.secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PaymentCard(
    heading: String,
    frequency: String,
    amount: Double,
    translatedPaymentDate: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.cardDefault
        )
    ) {
        PaymentHeader(text = heading)
        PaymentFrequency(text = frequency)
        PaymentAmount(amount = amount)
        PaymentDate(text = translatedPaymentDate)
        PaymentHistory()
    }
}

@Composable
private fun PaymentHeader(
    text: String,
    modifier: Modifier = Modifier
) {
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

        // TODO: What happens when this is clicked?
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = GovUkTheme.colourScheme.surfaces.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_more_horiz_24),
                contentDescription = "",
                tint = Color.White
            )
        }
    }
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
    text: String,
    modifier: Modifier = Modifier
) {
    // TODO: Needs formatting, e.g. 1 November 2027 (in 2 days)
    // TODO: Is using this https://github.com/jacobras/Human-Readable too much or roll our own?

    BodyRegularLabel(
        text = text,
        color = GovUkTheme.colourScheme.textAndIcons.secondary,
        modifier = modifier.padding(start = GovUkTheme.spacing.medium, end = GovUkTheme.spacing.medium, bottom = 12.dp)
    )
}

@Composable
private fun PaymentHistory(
    modifier: Modifier = Modifier
) {
    // TODO: What does this link to: the history page or filtered query?

    ListDivider(
        modifier = modifier.fillMaxWidth().padding(horizontal = GovUkTheme.spacing.medium)
    )

    Row(
        modifier = Modifier.fillMaxWidth().padding(start = GovUkTheme.spacing.medium, end = GovUkTheme.spacing.medium, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodyRegularLabel(
            text = "Payment history",
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
private fun UniversalCreditCard(
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
                    text = "Sign in to your Universal Credit account",
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
                text = "We can't show this payment information in the app yet.",
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        }
    }
}

fun Double.convert(): String {
    val format = DecimalFormat("#,###.00")
    format.isDecimalSeparatorAlwaysShown = false
    return format.format(this).toString()
}

@PreviewLightDark
@Composable
internal fun CurrentPaymentsScreenPreview() {
    GovUkTheme {
        CurrentPaymentsScreen(
            modifier = Modifier.background(GovUkTheme.colourScheme.surfaces.background)
        )
    }
}


private fun latestUpcomingPayments() : AwardDetailsResponse {
    val json = """
        {
            "statusCode": 200,
            "status": "success",
            "data": {
                "ame_last_issued": {
                    "statusCode": 200,
                    "status": "success",
                    "data": [
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitShortName": "PIP",
                                    "benefitName": "Personal Independence Payment",
                                    "amount": 1000.5,
                                    "benefitCode": 5100
                                },
                                {
                                    "benefitShortName": "PIP",
                                    "benefitName": "Personal Independence Payment",
                                    "amount": 999.5,
                                    "benefitCode": 5100
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 3088,
                            "deductionValue": 0.5,
                            "benefitCodes": [
                                5100
                            ],
                            "amount": 2000,
                            "translatedDueDate": "14 November 2026"
                        },
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 4100,
                                    "benefitShortName": "AA",
                                    "benefitName": "Attendance Allowance",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 4909,
                            "deductionValue": 100,
                            "benefitCodes": [
                                4100
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        },
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 5500,
                                    "benefitShortName": "PC",
                                    "benefitName": "Pension Credit",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 1239,
                            "deductionValue": 100,
                            "benefitCodes": [
                                5500
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        },
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 2300,
                                    "benefitShortName": "IS",
                                    "benefitName": "Income Support",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 3211,
                            "deductionValue": 100,
                            "benefitCodes": [
                                2300
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        },
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 4300,
                                    "benefitShortName": "JSA",
                                    "benefitName": "Jobseekers Allowance",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 8909,
                            "deductionValue": 100,
                            "benefitCodes": [
                                4300
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        },
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 4300,
                                    "benefitShortName": "IB",
                                    "benefitName": "Incapacity Benefit",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 8909,
                            "deductionValue": 100,
                            "benefitCodes": [
                                4300
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        }
                    ]
                },
                "ame_upcoming": {
                    "statusCode": 200,
                    "status": "success",
                    "data": [
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 4000,
                                    "benefitShortName": "DLA",
                                    "benefitName": "Disability Living Allowance",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 4000,
                            "deductionValue": 100,
                            "benefitCodes": [
                                4000
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        },
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 5200,
                                    "benefitShortName": "SP",
                                    "benefitName": "State Pension",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 9890,
                            "deductionValue": 100,
                            "benefitCodes": [
                                5200
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        },
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 5500,
                                    "benefitShortName": "ESA",
                                    "benefitName": "Employment and Support Allowance",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 5455,
                            "deductionValue": 100,
                            "benefitCodes": [
                                5500
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        },
                        {
                            "status": "ISS",
                            "statusCode": 1,
                            "statusDescription": "Issued",
                            "amountsPerBenefitCode": [
                                {
                                    "benefitCode": 5500,
                                    "benefitShortName": "CA",
                                    "benefitName": "Carer's Allowance",
                                    "amount": 500
                                }
                            ],
                            "issueDate": "2026-11-14",
                            "dueDate": "2026-11-14",
                            "netAmount": 5455,
                            "deductionValue": 100,
                            "benefitCodes": [
                                5500
                            ],
                            "amount": 500,
                            "translatedDueDate": "14 November 2026"
                        }
                    ]
                },
                "sas_award_details": {
                    "AA": {
                        "statusCode": 200,
                        "status": "success",
                        "data": {
                            "award_name": "attendance_allowance",
                            "award_start_date": "1995-01-09",
                            "payment_frequency": null,
                            "award_amount_breakdown": [
                                {
                                    "name": "care",
                                    "amount": 250,
                                    "rate": "high",
                                    "startDate": "2018-01-01"
                                }
                            ],
                            "deduction_amount_breakdown": null
                        }
                    },
                    "DLA": {
                        "statusCode": 200,
                        "status": "success",
                        "data": {
                            "award_name": "disability_living_allowance",
                            "award_start_date": "2000-09-28",
                            "award_end_date": null,
                            "award_type": null,
                            "payment_frequency": null,
                            "award_amount": null,
                            "deduction_amount": null,
                            "award_amount_breakdown": [
                                {
                                    "name": "mobility",
                                    "amount": 990,
                                    "rate": "high",
                                    "startDate": "2000-09-28",
                                    "endDate": null
                                },
                                {
                                    "name": "care",
                                    "amount": 990,
                                    "rate": "medium",
                                    "startDate": "2000-09-28",
                                    "endDate": "2026-09-01"
                                }
                            ],
                            "deduction_amount_breakdown": null
                        }
                    },
                    "ESA": {
                        "statusCode": 404,
                        "status": "failure",
                        "message": "Request failed with status code 404"
                    },
                    "PIP": {
                        "statusCode": 200,
                        "status": "success",
                        "data": {
                            "award_name": "personal_independence_payment",
                            "award_start_date": "0000-00-00 [yyyy-mm-dd].",
                            "award_end_date": "0000-00-00 [yyyy-mm-dd].",
                            "award_type": "other",
                            "payment_frequency": null,
                            "award_amount": 2000,
                            "deduction_amount": 0.5,
                            "award_amount_breakdown": [
                                {
                                    "name": "mobility",
                                    "amount": 1999.5,
                                    "rate": "standard",
                                    "startDate": "2020-05-02",
                                    "endDate": "2029-12-02"
                                },
                                {
                                    "name": "daily_living",
                                    "amount": 1999.5,
                                    "rate": "enhanced",
                                    "startDate": "2019-06-06"
                                }
                            ],
                            "deduction_amount_breakdown": null
                        }
                    },
                    "SP": {
                        "statusCode": 500,
                        "status": "failure",
                        "message": "Invalid URL"
                    },
                    "IS": {
                        "statusCode": 500,
                        "status": "failure",
                        "message": "Invalid URL"
                    },
                    "JSA": {
                        "statusCode": 404,
                        "status": "failure",
                        "message": "Request failed with status code 404"
                    },
                    "PC": {
                        "statusCode": 500,
                        "status": "failure",
                        "message": "Invalid URL"
                    },
                    "IB": {
                        "statusCode": 500,
                        "status": "failure",
                        "message": "Invalid URL"
                    },
                    "SDA": {
                        "statusCode": 500,
                        "status": "failure",
                        "message": "Invalid URL"
                    },
                    "CA": {
                        "statusCode": 500,
                        "status": "failure",
                        "message": "Invalid URL"
                    }
                }
            }
        }        
    """.trimIndent()

    return Gson().fromJson(json, AwardDetailsResponse::class.java)
}