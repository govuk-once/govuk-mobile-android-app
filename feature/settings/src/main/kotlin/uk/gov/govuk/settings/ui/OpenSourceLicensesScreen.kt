package uk.gov.govuk.settings.ui

import android.content.Context
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
import androidx.compose.ui.res.stringResource
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries
import com.mikepenz.aboutlibraries.util.withContext
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import uk.gov.govuk.settings.R

@Composable
internal fun OpenSourceLicensesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val libraries by rememberLibraries { context ->
        context.loadLibrariesWithDvlaFont()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.background)
    ) {
        ChildPageHeader(
            dismissStyle = HeaderDismissStyle.Back(onBack)
        )
        Title(
            title = stringResource(R.string.open_source_licenses_title)
        )

        MaterialTheme(
            typography = Typography(
                titleLarge = GovUkTheme.typography.bodyRegular
            ),
            colorScheme = MaterialTheme.colorScheme.copy(
                background = GovUkTheme.colourScheme.surfaces.background,
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

private fun Context.loadLibrariesWithDvlaFont(): Libs {
    val baseLibs = Libs.Builder().withContext(this).build()

    val licenseText = runCatching {
        resources.openRawResource(uk.gov.govuk.design.R.raw.dvla_vrm_ofl).reader().readText()
    }.getOrDefault("License text not found.")

    val dvlaVrmLicense = License(
        name = "DVLA - VRM License",
        url = null,
        licenseContent = licenseText,
        hash = "dvla_vrm_ofl_hash"
    )

    val dvlaVrmFont = Library(
        uniqueId = "dvla_vrm_font",
        name = getString(R.string.dvla_vrm_font_license_title),
        artifactVersion = null,
        description = null,
        website = null,
        developers = persistentListOf(),
        organization = null,
        scm = null,
        licenses = setOf(dvlaVrmLicense).toImmutableSet()
    )

    return Libs(
        libraries = (baseLibs.libraries + dvlaVrmFont).sortedBy { it.name.lowercase() }.toImmutableList(),
        licenses = (baseLibs.licenses + dvlaVrmLicense).toImmutableSet()
    )
}