package io.github.dustin2.dockclock.feature.appselection

import app.cash.turbine.test
import io.github.dustin2.dockclock.feature.settings.FakeSettingsRepository
import io.github.dustin2.dockclock.feature.settings.UserSettings
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppSelectionViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val defaultLocale = Locale.getDefault()
    private val source = FakeInstalledAppsSource(
        listOf(
            app("com.example.zoom", "Zoom"),
            app("com.example.agenda", "agenda"),
            app("com.example.edge", "Édge")
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        // Collator ordering depends on the locale, so pin it.
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        Locale.setDefault(defaultLocale)
    }

    @Test
    fun `starts loading, then lists apps alphabetically ignoring case and accents`() =
        runTest(dispatcher) {
            AppSelectionViewModel(source, FakeSettingsRepository()).uiState.test {
                assertEquals(AppSelectionUiState.Loading, awaitItem())
                assertEquals(
                    listOf("agenda", "Édge", "Zoom"),
                    (awaitItem() as AppSelectionUiState.Ready).apps.map {
                        it.label
                    }
                )
            }
        }

    @Test
    fun `marks hidden apps as not shown`() = runTest(dispatcher) {
        val settings = FakeSettingsRepository(UserSettings(hiddenApps = setOf("com.example.zoom")))

        AppSelectionViewModel(source, settings).uiState.test {
            skipItems(1)
            val shown = (awaitItem() as AppSelectionUiState.Ready).apps.associate {
                it.packageName to
                    it.isShown
            }
            assertEquals(
                mapOf(
                    "com.example.agenda" to true,
                    "com.example.edge" to true,
                    "com.example.zoom" to false
                ),
                shown
            )
        }
    }

    @Test
    fun `toggling an app saves it as hidden or shown`() = runTest(dispatcher) {
        val settings = FakeSettingsRepository()
        val viewModel = AppSelectionViewModel(source, settings)

        viewModel.onAppShownChange("com.example.zoom", shown = false)
        testScheduler.runCurrent()
        assertTrue("com.example.zoom" in settings.settings.value.hiddenApps)

        viewModel.onAppShownChange("com.example.zoom", shown = true)
        testScheduler.runCurrent()
        assertEquals(emptySet<String>(), settings.settings.value.hiddenApps)
    }

    private fun app(packageName: String, label: String) =
        InstalledApp(packageName, label, icon = null)
}

private class FakeInstalledAppsSource(private val apps: List<InstalledApp>) : InstalledAppsSource {
    override suspend fun launchableApps() = apps
}
