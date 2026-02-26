package uk.govuk.app.local.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.MediumHorizontalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.govuk.app.local.LocalConfirmationViewModel
import uk.govuk.app.local.R
import uk.govuk.app.local.domain.model.LocalAuthority

@Composable
internal fun LocalConfirmationRoute(
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LocalConfirmationViewModel = hiltViewModel()
    val localAuthority = viewModel.localAuthority

    LocalConfirmationScreen(
        onBack = onBack,
        onCancel = onCancel,
        onPageView = { viewModel.onPageView() },
        localAuthority = localAuthority,
        onDone = { text ->
            viewModel.onDone(text)
            onDone()
        },
        modifier = modifier
    )
}

@Composable
private fun LocalConfirmationScreen(
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onPageView: () -> Unit,
    localAuthority: LocalAuthority,
    onDone: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Scaffold(
        containerColor = GovUkTheme.colourScheme.surfaces.background,
        modifier = modifier.fillMaxWidth(),

        topBar = {
            LocalFullScreenHeader(onBack = onBack, onCancel = onCancel)
        },
        bottomBar = {
            val buttonText = stringResource(R.string.local_confirmation_button)
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(GovUkTheme.colourScheme.surfaces.background)
            ) {
                FixedPrimaryButton(
                    text = buttonText,
                    onClick = { onDone(buttonText) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .fillMaxWidth()
        ) {
            SmallVerticalSpacer()

            if (localAuthority.parent == null) {
                Unitary(localAuthority)
            } else {
                TwoTier(localAuthority)
            }

            MediumVerticalSpacer()
        }
    }
}

@Composable
private fun Unitary(
    localAuthority: LocalAuthority,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Title1BoldLabel(
            text = stringResource(R.string.local_confirmation_unitary_title, localAuthority.name),
            modifier = Modifier.semantics { heading() }
        )

        MediumVerticalSpacer()

        BodyRegularLabel(
            text = stringResource(R.string.local_confirmation_unitary_description)
        )
    }
}

@Composable
private fun TwoTier(
    localAuthority: LocalAuthority,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Title1BoldLabel(
            text = stringResource(R.string.local_confirmation_two_tier_title),
            modifier = Modifier.semantics { heading() }
        )

        MediumVerticalSpacer()

        BodyRegularLabel(
            text = stringResource(R.string.local_confirmation_two_tier_description_1)
        )

        MediumVerticalSpacer()

        BodyRegularLabel(
            text = stringResource(R.string.local_confirmation_two_tier_bullet_title)
        )

        MediumVerticalSpacer()

        Column(Modifier.padding(start = GovUkTheme.spacing.small)) {
            BulletItem(localAuthority.parent?.name ?: "")
            BulletItem(localAuthority.name)
        }

        MediumVerticalSpacer()

        BodyRegularLabel(
            text = stringResource(R.string.local_confirmation_two_tier_description_2)
        )
    }
}

@Composable
private fun BulletItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(GovUkTheme.colourScheme.textAndIcons.primary)
        )
        MediumHorizontalSpacer()
        BodyRegularLabel(text)
    }
}

@Preview
@Composable
private fun UnitaryLocalAuthorityPreview() {
    GovUkTheme {
        LocalConfirmationScreen(
            onBack = { },
            onCancel = { },
            onPageView = { },
            localAuthority = LocalAuthority(
                name = "Bristol City Council",
                url = "",
                slug = ""
            ),
            onDone = { }
        )
    }
}

@Preview
@Composable
private fun TwoTierLocalAuthorityPreview() {
    GovUkTheme {
        LocalConfirmationScreen(
            onBack = { },
            onCancel = { },
            onPageView = { },
            localAuthority = LocalAuthority(
                name = "Derbyshire City Council",
                url = "",
                slug = "",
                parent = LocalAuthority(
                    name = "Derbyshire Dales District Council",
                    url = "",
                    slug = "",
                )
            ),
            onDone = { }
        )
    }
}