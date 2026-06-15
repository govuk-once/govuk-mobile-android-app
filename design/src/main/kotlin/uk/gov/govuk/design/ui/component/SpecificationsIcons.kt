package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.model.SpecificationUiModel
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun SpecificationsIcons(
    icons: List<SpecificationUiModel>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
    ) {
        icons.forEachIndexed { index, icon ->
            val startCorners = if (index == 0) GovUkTheme.numbers.cornerAndroidList else 0.dp
            val endCorners =
                if (index == icons.size - 1) GovUkTheme.numbers.cornerAndroidList else 0.dp
            val roundedCornerShape =
                RoundedCornerShape(startCorners, endCorners, endCorners, startCorners)

            Column(
                modifier = Modifier
                    .padding(start = if (index == 0) 0.dp else 1.dp)
                    .clip(roundedCornerShape)
                    .background(GovUkTheme.colourScheme.surfaces.listAlt)
                    .padding(GovUkTheme.spacing.medium)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                icon.icon?.let {
                    Image(
                        painter = painterResource(it),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        colorFilter = ColorFilter.tint(GovUkTheme.colourScheme.textAndIcons.primary)
                    )
                }
                SmallVerticalSpacer()
                BodyRegularLabel(
                    text = icon.description
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SpecificationsIconsPreview() {
    val specificationsIcons = listOf(
        SpecificationUiModel(
            icon = R.drawable.ic_home,
            description = "Home",
            altText = ""
        ),
        SpecificationUiModel(
            icon = R.drawable.crown,
            description = "Crown",
            altText = ""
        ),
        SpecificationUiModel(
            icon = R.drawable.ic_settings,
            description = "Settings",
            altText = ""
        )
    )
    GovUkTheme {
        SpecificationsIcons(
            icons = specificationsIcons
        )
    }
}
