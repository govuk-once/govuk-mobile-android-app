package uk.gov.govuk.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.Title
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.getValue
import com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries

@Composable
internal fun OpenSourceLicensesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val libraries by rememberLibraries()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.screenBackground)
    ) {
        ChildPageHeader(
            dismissStyle = HeaderDismissStyle.Back(onBack)
        )
        Title(
            title = "Open source licenses"
        )

        MaterialTheme(
            typography = Typography(
                titleLarge = GovUkTheme.typography.bodyRegular
            ),
            colorScheme = MaterialTheme.colorScheme.copy(
                background = GovUkTheme.colourScheme.surfaces.screenBackground,
                onBackground = GovUkTheme.colourScheme.textAndIcons.primary
            )
        ) {
            LibrariesContainer(
                libraries = libraries,
                modifier = Modifier.fillMaxSize(),
                showDescription = false,
                showLicenseBadges = false,
                showAuthor = false,
                showVersion = false
            )
        }
    }
}