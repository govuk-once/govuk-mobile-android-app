package uk.gov.govuk

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.navigation.AppNavigation
import uk.gov.govuk.ui.GovUkApp
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    internal lateinit var appNavigation: AppNavigation

    private val _intentFlow: MutableSharedFlow<Intent> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    internal val intentFlow = _intentFlow.asSharedFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        emitIntent(savedInstanceState)

        setContent {
            GovUkTheme {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    color = GovUkTheme.colourScheme.surfaces.background
                ) {
                    GovUkApp(_intentFlow, appNavigation)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        _intentFlow.tryEmit(intent)
    }

    private fun emitIntent(savedInstanceState: Bundle?) {
        // Only emit intent when app launched from cold so deep links only ever run once
        savedInstanceState ?: run {
            _intentFlow.tryEmit(intent)
        }
    }
}