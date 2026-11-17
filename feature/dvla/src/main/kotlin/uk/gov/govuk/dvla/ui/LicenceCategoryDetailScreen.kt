package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.ListDivider
import uk.gov.govuk.design.ui.component.RunOnceLaunchedEffect
import uk.gov.govuk.design.ui.component.SectionHeadingLabel
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.LicenceCategoryDetailUiModel
import uk.gov.govuk.dvla.ui.model.LicenceCategoryRestrictionUiModel
import uk.gov.govuk.dvla.ui.model.LicenceCategoryUiModel

@Composable
internal fun LicenceCategoryDetailScreen(
    detail: LicenceCategoryDetailUiModel,
    onBack: () -> Unit,
    onPageView: (title: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val category = detail.category
    val categoryAltText = stringResource(R.string.licence_category_alt_text, category.code)

    RunOnceLaunchedEffect {
        onPageView(detail.title)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(GovUkTheme.colourScheme.surfaces.background)
            .safeDrawingPadding()
    ) {
        FullScreenHeader(dismissStyle = HeaderDismissStyle.Back(onBack))

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            LargeTitleBoldLabel(
                text = detail.title,
                modifier = Modifier.semantics { heading() }
            )

            Spacer(Modifier.height(48.dp))

            BodyRegularLabel(
                text = stringResource(R.string.licence_category_detail_title),
                modifier = Modifier.clearAndSetSemantics { contentDescription = categoryAltText }
            )

            SmallVerticalSpacer()

            CategoryBadge(category = category)

            detail.validFrom?.let {
                ValidityRow(
                    label = stringResource(R.string.licence_category_detail_valid_from),
                    date = it
                )
            }
            detail.validTo?.let {
                ValidityRow(
                    label = stringResource(R.string.licence_category_detail_valid_to),
                    date = it
                )
            }

            // restrictions
            if (detail.restrictions.isNotEmpty()) {
                Spacer(Modifier.height(48.dp))
                SectionHeadingLabel(title3 = stringResource(R.string.licence_category_detail_restrictions_header))
                detail.restrictions.forEachIndexed { index, restriction ->
                    if (index > 0) Spacer(Modifier.height(GovUkTheme.spacing.medium))
                    RestrictionRow(restriction = restriction)
                }
            }

            // you can drive
            detail.youCanDrive?.let {
                Spacer(Modifier.height(48.dp))
                SectionHeadingLabel(title3 = stringResource(R.string.licence_category_detail_you_can_drive_header))
                BodyRegularLabel(text = it)
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun CategoryBadge(
    category: LicenceCategoryUiModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = GovUkTheme.spacing.medium)
            .height(IntrinsicSize.Min)
            .clearAndSetSemantics { },
        verticalAlignment = Alignment.CenterVertically
    ) {
        category.icon?.let { icon ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(GovUkTheme.spacing.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.primary
                )
            }

            VerticalDivider(color = GovUkTheme.colourScheme.strokes.listDivider)
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(GovUkTheme.spacing.medium),
            contentAlignment = Alignment.Center
        ) {
            LargeTitleBoldLabel(text = category.code)
        }
    }
}

@Composable
private fun ValidityRow(
    label: String,
    date: String
) {
    ListDivider(Modifier.fillMaxWidth())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = GovUkTheme.spacing.medium)
            .semantics(mergeDescendants = true) { }
    ) {
        BodyRegularLabel(text = label, modifier = Modifier.weight(1f))
        BodyBoldLabel(text = date)
    }
}

@Composable
private fun RestrictionRow(
    restriction: LicenceCategoryRestrictionUiModel
) {
    val altText = stringResource(
        R.string.licence_category_detail_restriction_alt_text,
        restriction.code,
        restriction.description
    )
    val codeStyle = GovUkTheme.typography.bodyBold.toSpanStyle()

    BodyRegularLabel(
        text = buildAnnotatedString {
            withStyle(codeStyle) { append(restriction.code) }
            append(" ")
            append(restriction.description)
        },
        modifier = Modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = altText }
    )
}

@PreviewLightDark
@Composable
internal fun LicenceCategoryDetailScreenPreview() {
    GovUkTheme {
        Box(modifier = Modifier.background(GovUkTheme.colourScheme.surfaces.background)) {
            LicenceCategoryDetailScreen(
                detail = LicenceCategoryDetailUiModel(
                    title = "Minibuses",
                    category = LicenceCategoryUiModel("D1E", R.drawable.ic_licence_cat_d1e),
                    expiryIndicator = null,
                    validFrom = "21 December 1990",
                    validTo = "23 May 2043",
                    restrictions = listOf(
                        LicenceCategoryRestrictionUiModel("101", "Not for hire or reward"),
                        LicenceCategoryRestrictionUiModel(
                            "122",
                            "Valid on successful completion of Basic Moped Training Course (CBT)"
                        )
                    ),
                    youCanDrive = "Minibuses with a trailer, within the weight limits for this category."
                ),
                onBack = {},
                onPageView = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
internal fun LicenceCategoryDetailScreenNoIconPreview() {
    GovUkTheme {
        Box(modifier = Modifier.background(GovUkTheme.colourScheme.surfaces.background)) {
            LicenceCategoryDetailScreen(
                detail = LicenceCategoryDetailUiModel(
                    title = "Other vehicles",
                    category = LicenceCategoryUiModel("f"),
                    expiryIndicator = null,
                    validFrom = "27 January 2006",
                    validTo = "17 September 2051",
                    restrictions = emptyList(),
                    youCanDrive = "Agricultural"
                ),
                onBack = {},
                onPageView = {}
            )
        }
    }
}
