package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.model.SpecificationIconUiModel
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun SpecificationsIcons(
    uiModels: List<SpecificationIconUiModel>,
    modifier: Modifier = Modifier
) {
    var shouldShowVertical by remember { mutableStateOf(false) }

    if (shouldShowVertical) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            uiModels.forEachIndexed { index, uiModel ->
                VerticalSpecificationItem(
                    uiModel = uiModel,
                    modifier = Modifier
                        .getModifier(
                            index = index,
                            maxIndex = uiModels.lastIndex,
                            isHorizontal = false
                        )
                        .fillMaxWidth()
                )
            }
        }
    } else {
        Row(
            modifier = modifier
                .height(IntrinsicSize.Min)
        ) {
            uiModels.forEachIndexed { index, uiModel ->
                HorizontalSpecificationItem(
                    uiModel = uiModel,
                    modifier = Modifier
                        .getModifier(
                            index = index,
                            maxIndex = uiModels.lastIndex,
                            isHorizontal = true
                        )
                        .weight(1f),
                    onTruncatedText = {
                        if (!shouldShowVertical) {
                            shouldShowVertical = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun Modifier.getModifier(
    index: Int,
    maxIndex: Int,
    isHorizontal: Boolean
): Modifier {
    val firstCorners = if (index == 0) GovUkTheme.numbers.cornerAndroidList else 0.dp
    val lastCorners =
        if (index == maxIndex) GovUkTheme.numbers.cornerAndroidList else 0.dp
    val padding = if (index == 0) 0.dp else 1.dp
    return if (isHorizontal)
        this
            .clip(RoundedCornerShape(firstCorners, lastCorners, lastCorners, firstCorners))
            .padding(start = padding)
    else
        this
            .clip(RoundedCornerShape(firstCorners, firstCorners, lastCorners, lastCorners))
            .padding(top = padding)
}

@Composable
private fun HorizontalSpecificationItem(
    uiModel: SpecificationIconUiModel,
    modifier: Modifier = Modifier,
    onTruncatedText: () -> Unit
) {
    Column(
        modifier = modifier
            .background(GovUkTheme.colourScheme.surfaces.listAlt)
            .padding(GovUkTheme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(uiModel.icon),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(GovUkTheme.colourScheme.textAndIcons.primary)
        )

        SmallVerticalSpacer()

        Text(
            text = uiModel.description,
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            style = GovUkTheme.typography.bodyRegular,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.hasVisualOverflow) {
                    onTruncatedText()
                }
            },
            modifier = Modifier.semantics {
                contentDescription = uiModel.altText
            }
        )
    }
}

@Composable
private fun VerticalSpecificationItem(
    uiModel: SpecificationIconUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(GovUkTheme.colourScheme.surfaces.listAlt)
            .padding(GovUkTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(uiModel.icon),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(GovUkTheme.colourScheme.textAndIcons.primary)
        )

        MediumHorizontalSpacer()

        BodyRegularLabel(
            text = uiModel.description,
            modifier = Modifier.semantics {
                contentDescription = uiModel.altText
            }
        )
    }
}

@PreviewLightDark
@Composable
private fun HorizontalSpecificationsIconsPreview() {
    val uiModels = listOf(
        SpecificationIconUiModel(
            icon = R.drawable.ic_home,
            description = "Home",
            altText = ""
        ),
        SpecificationIconUiModel(
            icon = R.drawable.crown,
            description = "Crown",
            altText = ""
        ),
        SpecificationIconUiModel(
            icon = R.drawable.ic_settings,
            description = "Settings",
            altText = ""
        )
    )
    GovUkTheme {
        SpecificationsIcons(
            uiModels = uiModels
        )
    }
}

@PreviewLightDark
@Composable
private fun VerticalSpecificationsIconsPreview() {
    val uiModels = listOf(
        SpecificationIconUiModel(
            icon = R.drawable.ic_home,
            description = "Home",
            altText = ""
        ),
        SpecificationIconUiModel(
            icon = R.drawable.crown,
            description = "Crown with really long description",
            altText = ""
        ),
        SpecificationIconUiModel(
            icon = R.drawable.ic_settings,
            description = "Settings",
            altText = ""
        )
    )
    GovUkTheme {
        SpecificationsIcons(
            uiModels = uiModels
        )
    }
}
