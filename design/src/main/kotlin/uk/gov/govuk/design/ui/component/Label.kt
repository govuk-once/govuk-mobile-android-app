package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.model.SectionHeadingLabelButton
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
private fun BaseLabel(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    color: Color,
    textAlign: TextAlign,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    ) {

    BaseLabel(
        text = AnnotatedString(text),
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
        onTextLayout = onTextLayout
    )
}

@Composable
private fun BaseLabel(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle,
    color: Color,
    textAlign: TextAlign,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    val altText = text.text.replace(
        stringResource(R.string.gov_uk),
        stringResource(R.string.gov_uk_alt_text)
    )
    Text(
        text = text,
        modifier = modifier.semantics {
            contentDescription = altText
        },
        style = style.copy(hyphens = Hyphens.Auto),
        color = color,
        textAlign = textAlign,
        onTextLayout = onTextLayout ?: {}
    )
}

@Composable
fun LargeTitleBoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.titleLargeBold,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun LargeTitleRegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.titleLargeRegular,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun Title1BoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.title1Bold,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun Title1RegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.title1Regular,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun Title2BoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.title2Bold,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun Title2RegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.title2Regular,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun Title3BoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.title3Bold,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun Title3RegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.title3Regular,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun BodyBoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.bodyBold,
        color = color,
        textAlign = textAlign,
        onTextLayout = onTextLayout
    )
}

@Composable
fun BodyRegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.bodyRegular,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun BodyRegularLabel(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.bodyRegular,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun BodyRegularLabelTrailingLink(
    introText: String,
    outroText: String,
    linkText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    altText: String? = null,
    textColor: Color = GovUkTheme.colourScheme.textAndIcons.secondary,
    textAlign: TextAlign = TextAlign.Left
) {
    val linkColor = GovUkTheme.colourScheme.textAndIcons.link
    val highlightColor = textColor.copy(alpha = 0.12f)  // Material 3's standard ripple colour

    val annotatedString = buildAnnotatedString {
        append(introText)
        append(" ")
        val link = LinkAnnotation.Clickable(
            tag = "link",
            styles = TextLinkStyles(
                style = SpanStyle(color = linkColor, textDecoration = TextDecoration.None),
                pressedStyle = SpanStyle(background = highlightColor)
            ),
            linkInteractionListener = { _ -> onClick() }
        )
        withLink(link) { append(linkText) }
        append(outroText)
    }

    BodyRegularLabel(
        text = annotatedString,
        textAlign = textAlign,
        modifier = modifier
            .clearAndSetSemantics {
                contentDescription = altText ?: "$introText $linkText"
                onClick(action = {
                    onClick()
                    true
                })
            },
        color = textColor
    )
}

@Composable
fun CalloutBoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.calloutBold,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun CalloutRegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.calloutRegular,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun SubheadlineBoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.subheadlineBold,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun SubheadlineRegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.subheadlineRegular,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun FootnoteBoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.footnoteBold,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun FootnoteRegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.footnoteRegular,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun CaptionBoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.captionBold,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun CaptionRegularLabelTrailingLink(
    text: String,
    linkText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    altText: String? = null,
    textColor: Color = GovUkTheme.colourScheme.textAndIcons.secondary
) {
    val linkColor = GovUkTheme.colourScheme.textAndIcons.link
    val highlightColor = textColor.copy(alpha = 0.12f)  // Material 3's standard ripple colour

    val annotatedString = buildAnnotatedString {
        append(text)
        append(" ")
        val link = LinkAnnotation.Clickable(
            tag = "link",
            styles = TextLinkStyles(
                style = SpanStyle(color = linkColor, textDecoration = TextDecoration.None),
                pressedStyle = SpanStyle(background = highlightColor)
            ),
            linkInteractionListener = { _ -> onClick() }
        )
        withLink(link) { append(linkText) }
    }

    CaptionRegularLabel(
        text = annotatedString,
        modifier = modifier
            .clearAndSetSemantics {
                contentDescription = altText ?: "$text $linkText"
                onClick(action = {
                    onClick()
                    true
                })
            },
        color = textColor
    )
}

@Composable
fun CaptionRegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.secondary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.captionRegular,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun CaptionRegularLabel(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = GovUkTheme.colourScheme.textAndIcons.secondary,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.captionRegular,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun ListHeadingLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Title3BoldLabel(
        text = text,
        modifier = modifier
            .padding(
                start = GovUkTheme.spacing.medium,
                end = GovUkTheme.spacing.medium,
            )
            .semantics { heading() }
    )
}

@Composable
fun SectionHeadingLabel(
    modifier: Modifier = Modifier,
    title1: String? = null,
    title3: String? = null,
    button: SectionHeadingLabelButton? = null
) {
    Row(
        modifier = modifier.padding(bottom = GovUkTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val titleModifier = Modifier
            .padding(
                vertical = GovUkTheme.spacing.medium
            )
            .weight(1f)
            .semantics { heading() }
        val titleColour = GovUkTheme.colourScheme.textAndIcons.primary

        title1?.let { title1 ->
            Title1BoldLabel(
                text = title1,
                modifier = titleModifier,
                color = titleColour
            )
        }

        title3?.let { title3 ->
            Title3BoldLabel(
                text = title3,
                modifier = titleModifier,
                color = titleColour
            )
        }

        button?.let { button ->
            TextButton(
                onClick = button.onClick,
                modifier = Modifier.padding(
                    top = GovUkTheme.spacing.extraSmall,
                    start = GovUkTheme.spacing.medium,
                    bottom = GovUkTheme.spacing.extraSmall
                ),
                contentPadding = PaddingValues()
            ) {
                BodyRegularLabel(
                    text = button.title,
                    modifier = Modifier
                        .background(GovUkTheme.colourScheme.surfaces.cardDefault)
                        .padding(
                            horizontal = GovUkTheme.spacing.medium,
                            vertical = 9.dp
                        )
                        .semantics { contentDescription = button.altText },
                    color = GovUkTheme.colourScheme.textAndIcons.linkSecondary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LargeTitleBold() {
    GovUkTheme {
        LargeTitleBoldLabel("Large Title Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun LargeTitleRegular() {
    GovUkTheme {
        LargeTitleRegularLabel("Large Title Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun Title1Bold() {
    GovUkTheme {
        Title1BoldLabel("Title 1 Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun Title1Regular() {
    GovUkTheme {
        Title1RegularLabel("Title 1 Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun Title2Bold() {
    GovUkTheme {
        Title2BoldLabel("Title 2 Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun Title2Regular() {
    GovUkTheme {
        Title2RegularLabel("Title 2 Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun Title3Bold() {
    GovUkTheme {
        Title3BoldLabel("Title 3 Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun Title3Regular() {
    GovUkTheme {
        Title3RegularLabel("Title 3 Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun BodyBold() {
    GovUkTheme {
        BodyBoldLabel("Body Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun BodyRegular() {
    GovUkTheme {
        BodyRegularLabel("Body Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun CalloutBold() {
    GovUkTheme {
        CalloutBoldLabel("Callout Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun CalloutRegular() {
    GovUkTheme {
        CalloutRegularLabel("Callout Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun SubheadlineBold() {
    GovUkTheme {
        SubheadlineBoldLabel("Subheadline Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun SubheadlineRegular() {
    GovUkTheme {
        SubheadlineRegularLabel("Subheadline Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun FootnoteBold() {
    GovUkTheme {
        FootnoteBoldLabel("Footnote Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun FootnoteRegular() {
    GovUkTheme {
        FootnoteRegularLabel("Footnote Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun CaptionBold() {
    GovUkTheme {
        CaptionBoldLabel("Caption Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun CaptionRegular() {
    GovUkTheme {
        CaptionRegularLabel("Caption Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun ListHeadingLabelPreview() {
    GovUkTheme {
        ListHeadingLabel("Settings text")
    }
}

@Preview(showBackground = true)
@Composable
private fun SectionHeadingLabelTitle1Preview() {
    val button = SectionHeadingLabelButton(
        title = "Button Title",
        altText = "Alt text",
        onClick = {}
    )
    GovUkTheme {
        SectionHeadingLabel(
            title1 = "Section Heading Label",
            button = button
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SectionHeadingLabelTitle3Preview() {
    val button = SectionHeadingLabelButton(
        title = "Button Title",
        altText = "Alt text",
        onClick = {}
    )
    GovUkTheme {
        SectionHeadingLabel(
            title3 = "Section Heading Label",
            button = button
        )
    }
}
