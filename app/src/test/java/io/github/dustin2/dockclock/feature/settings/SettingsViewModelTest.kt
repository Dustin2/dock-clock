package io.github.dustin2.dockclock.feature.settings

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `reflects saved settings`() = runTest(dispatcher) {
        val viewModel =
            SettingsViewModel(FakeSettingsRepository(UserSettings(hideNotificationContent = true)))

        viewModel.uiState.test {
            assertFalse(awaitItem().hideNotificationContent)
            assertTrue(awaitItem().hideNotificationContent)
        }
    }

    @Test
    fun `toggling saves the new value`() = runTest(dispatcher) {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)

        viewModel.onHideNotificationContentChange(true)
        testScheduler.runCurrent()

        assertTrue(repository.settings.value.hideNotificationContent)
    }
}
