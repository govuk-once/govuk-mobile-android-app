package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ExternalLinkListItem
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.RunOnceLaunchedEffect
import uk.gov.govuk.design.ui.component.SectionHeadingLabel
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.component.CategoryListItem
import uk.gov.govuk.dvla.ui.model.LicenceCategoriesUiModel
import uk.gov.govuk.dvla.ui.model.LicenceCategoryGroupUiModel
import uk.gov.govuk.dvla.ui.model.LicenceCategoryLinkUiModel
import uk.gov.govuk.dvla.ui.model.LicenceCategoryUiModel

@Composable
internal fun LicenceCategoriesScreen(
    categories: LicenceCategoriesUiModel,
    onBack: () -> Unit,
    onPageView: () -> Unit,
    onCategoryInfoClick: (code: String) -> Unit,
    onFurtherInfoLinkClick: (text: String, url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    RunOnceLaunchedEffect { onPageView() }

    Column(modifier = modifier.safeDrawingPadding().fillMaxWidth()) {
        FullScreenHeader(dismissStyle = HeaderDismissStyle.Back(onBack))

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            LargeTitleBoldLabel(
                text = stringResource(R.string.licence_categories_title),
            )

            Spacer(Modifier.height(12.dp))

            BodyRegularLabel(text = stringResource(R.string.licence_categories_intro))

            ExtraLargeVerticalSpacer()

            CategorySection(
                title = stringResource(R.string.licence_categories_can_drive_header),
                groups = categories.canDrive,
                onInfoClick = onCategoryInfoClick
            )

            CategorySection(
                title = stringResource(R.string.licence_categories_provisionally_drive_header),
                groups = categories.provisionallyDrive,
                onInfoClick = onCategoryInfoClick
            )

            FurtherInformation(
                links = categories.furtherInformationLinks,
                onLinkClick = onFurtherInfoLinkClick
            )

            LargeVerticalSpacer()
        }
    }
}

@Composable
private fun CategorySection(
    title: String,
    groups: List<LicenceCategoryGroupUiModel>,
    onInfoClick: (code: String) -> Unit
) {
    if (groups.isEmpty()) return

    SectionHeadingLabel(title3 = title)

    groups.forEachIndexed { index, group ->
        if (index == 0) SmallVerticalSpacer() else Spacer(Modifier.height(36.dp))

        CategoryGroup(group = group, onInfoClick = onInfoClick)
    }

    // 48dp before the next section
    LargeVerticalSpacer()
    LargeVerticalSpacer()
}

@Composable
private fun CategoryGroup(
    group: LicenceCategoryGroupUiModel,
    onInfoClick: (code: String) -> Unit
) {
    BodyBoldLabel(text = group.title, modifier = Modifier.semantics { heading() })

    SmallVerticalSpacer()

    group.categories.forEachIndexed { index, category ->
        CategoryListItem(
            category = category,
            onInfoClick = { onInfoClick(category.code) },
            showDivider = index != group.categories.lastIndex
        )
    }
}

@Composable
private fun FurtherInformation(
    links: List<LicenceCategoryLinkUiModel>,
    onLinkClick: (text: String, url: String) -> Unit
) {
    if (links.isEmpty()) return

    SectionHeadingLabel(title3 = stringResource(R.string.licence_categories_further_information_header))

    links.forEachIndexed { index, link ->
        ExternalLinkListItem(
            title = link.text,
            onClick = { onLinkClick(link.text, link.url) },
            isFirst = index == 0,
            isLast = index == links.lastIndex
        )
    }
}

@PreviewLightDark
@Composable
private fun LicenceCategoriesScreenPreview() {
    GovUkTheme {
        Box(modifier = Modifier.background(GovUkTheme.colourScheme.surfaces.background)) {
            LicenceCategoriesScreen(
                categories = LicenceCategoriesUiModel(
                    canDrive = listOf(
                        LicenceCategoryGroupUiModel(
                            "Mopeds",
                            listOf(
                                LicenceCategoryUiModel("P", R.drawable.ic_licence_cat_p),
                                LicenceCategoryUiModel("AM", R.drawable.ic_licence_cat_am)
                            )
                        ),
                        LicenceCategoryGroupUiModel(
                            "Cars",
                            listOf(
                                LicenceCategoryUiModel("B", R.drawable.ic_licence_cat_b),
                                LicenceCategoryUiModel("BE", R.drawable.ic_licence_cat_be)
                            )
                        ),
                        LicenceCategoryGroupUiModel(
                            "Other vehicles",
                            listOf(LicenceCategoryUiModel("f"), LicenceCategoryUiModel("k"))
                        )
                    ),
                    provisionallyDrive = listOf(
                        LicenceCategoryGroupUiModel(
                            "Motorcycles",
                            listOf(LicenceCategoryUiModel("A", R.drawable.ic_licence_cat_a))
                        )
                    )
                ),
                onBack = {},
                onPageView = {},
                onCategoryInfoClick = {},
                onFurtherInfoLinkClick = { _, _ -> }
            )
        }
    }
}
