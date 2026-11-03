package io.github.dustin2.dockclock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.dustin2.dockclock.feature.appselection.AppSelectionScreen
import io.github.dustin2.dockclock.feature.permissions.NotificationAccessSection
import io.github.dustin2.dockclock.feature.screensaver.ScreenSaverSection
import io.github.dustin2.dockclock.feature.settings.SettingsSection
import kotlinx.serialization.Serializable

// Serializable so the back stack survives rotation and process death.
@Serializable
data object HomeRoute : NavKey

@Serializable
data object AppSelectionRoute : NavKey

/**
 * In-app navigation. The clock stays a separate activity because its orientation,
 * keep-screen-on and immersive settings belong to its own window.
 */
@Composable
fun DockClockApp(onStartClock: () -> Unit) {
    val backStack = rememberNavBackStack(HomeRoute)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        // The ViewModel decorator scopes each screen's ViewModel to its entry, so it is
        // cleared when the user navigates back instead of living as long as the activity.
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<HomeRoute> {
                HomeScreen(
                    onStartClock = onStartClock,
                    onChooseApps = { backStack.add(AppSelectionRoute) }
                )
            }
            entry<AppSelectionRoute> {
                AppSelectionScreen(onBack = { backStack.removeLastOrNull() })
            }
        }
    )
}

@Composable
private fun HomeScreen(onStartClock: () -> Unit, onChooseApps: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .safeDrawingPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NotificationAccessSection()
            SettingsSection(onChooseApps = onChooseApps)
            ScreenSaverSection()
            Button(onClick = onStartClock) {
                Text(stringResource(R.string.start_clock))
            }
        }
    }
}
