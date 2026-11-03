package io.github.dustin2.dockclock.feature.clock

import app.cash.turbine.test
import io.github.dustin2.dockclock.feature.notifications.AppInfo
import io.github.dustin2.dockclock.feature.notifications.AppInfoProvider
import io.github.dustin2.dockclock.feature.notifications.ClockNotification
import io.github.dustin2.dockclock.feature.notifications.InMemoryNotificationRepository
import io.github.dustin2.dockclock.feature.settings.FakeSettingsRepository
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClockViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val start = Instant.parse("2026-11-03T14:05:30Z")

    // Moves with the test scheduler, so `delay` in the ViewModel advances the clock too.
    private val clock = object : Clock() {
        override fun instant(): Instant = start.plusMillis(dispatcher.scheduler.currentTime)

        override fun getZone(): ZoneId = ZoneOffset.UTC

        override fun withZone(zone: ZoneId?): Clock = this
    }

    private val settings = FakeDeviceTimeSettings()
    private val notificationRepository = InMemoryNotificationRepository()
    private val settingsRepository = FakeSettingsRepository()

    private fun viewModel() = ClockViewModel(
        clock,
        settings,
        notificationRepository,
        settingsRepository,
        FakeAppInfoProvider()
    )

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `initial state shows current time and date`() {
        val state = viewModel().uiState.value

        assertEquals("14:05", state.time)
        assertEquals("Tuesday, November 3, 2026", state.date)
    }

    @Test
    fun `uses 12 hour format when the device does`() {
        settings.is24Hour = false

        assertEquals("2:05 PM", viewModel().uiState.value.time)
    }

    @Test
    fun `uses the device time zone`() {
        settings.zoneId = ZoneId.of("America/Mexico_City")

        assertEquals("08:05", viewModel().uiState.value.time)
    }

    @Test
    fun `updates exactly at the next minute boundary`() = runTest(dispatcher) {
        viewModel().uiState.test {
            assertEquals("14:05", awaitItem().time)

            advanceTimeByAndRun(29_999)
            expectNoEvents()

            advanceTimeByAndRun(1)
            assertEquals("14:06", awaitItem().time)
        }
    }

    @Test
    fun `moves the burn-in shift when the minute changes`() = runTest(dispatcher) {
        viewModel().uiState.test {
            val first = awaitItem().burnInShift

            advanceTimeByAndRun(30_000)
            assertNotEquals(first, awaitItem().burnInShift)
        }
    }

    @Test
    fun `shows active notifications with app info`() = runTest(dispatcher) {
        notificationRepository.replaceAll(listOf(notification(key = "a")))

        viewModel().uiState.test {
            skipItems(1)
            assertEquals(
                listOf(NotificationUiModel("a", "Label of com.example.chat", null, "Ana", "Hi")),
                awaitItem().notifications
            )
        }
    }

    @Test
    fun `hides title and text when content is hidden`() = runTest(dispatcher) {
        notificationRepository.replaceAll(listOf(notification(key = "a")))
        settingsRepository.setHideNotificationContent(true)

        viewModel().uiState.test {
            skipItems(1)
            assertEquals(
                listOf(NotificationUiModel("a", "Label of com.example.chat", null, null, null)),
                awaitItem().notifications
            )

            settingsRepository.setHideNotificationContent(false)
            assertEquals("Ana", awaitItem().notifications.single().title)
        }
    }

    @Test
    fun `leaves out notifications from hidden apps`() = runTest(dispatcher) {
        notificationRepository.replaceAll(
            listOf(
                notification(key = "a"),
                notification(key = "b", packageName = "com.example.mail")
            )
        )
        settingsRepository.setAppHidden("com.example.chat", hidden = true)

        viewModel().uiState.test {
            skipItems(1)
            assertEquals(listOf("b"), awaitItem().notifications.map { it.key })

            settingsRepository.setAppHidden("com.example.chat", hidden = false)
            assertEquals(listOf("a", "b"), awaitItem().notifications.map { it.key })
        }
    }

    @Test
    fun `initial state has no notifications until settings load`() {
        notificationRepository.replaceAll(listOf(notification(key = "a")))

        assertEquals(emptyList<NotificationUiModel>(), viewModel().uiState.value.notifications)
    }

    @Test
    fun `follows posted and removed notifications`() = runTest(dispatcher) {
        viewModel().uiState.test {
            assertEquals(emptyList<NotificationUiModel>(), awaitItem().notifications)

            notificationRepository.replaceAll(listOf(notification(key = "a")))
            assertEquals(listOf("a"), awaitItem().notifications.map { it.key })

            // Same as losing notification access: the listener clears the repository.
            notificationRepository.clear()
            assertEquals(emptyList<NotificationUiModel>(), awaitItem().notifications)
        }
    }

    private fun notification(key: String, packageName: String = "com.example.chat") =
        ClockNotification(key, packageName, "Ana", "Hi", postedAtMillis = 0)

    private fun TestScope.advanceTimeByAndRun(millis: Long) {
        advanceTimeBy(millis)
        testScheduler.runCurrent()
    }
}

private class FakeAppInfoProvider : AppInfoProvider {
    override fun appInfo(packageName: String) =
        AppInfo(label = "Label of $packageName", icon = null)
}

private class FakeDeviceTimeSettings : DeviceTimeSettings {
    var zoneId: ZoneId = ZoneOffset.UTC
    var is24Hour = true

    override fun zone(): ZoneId = zoneId

    override fun locale(): Locale = Locale.US

    override fun is24HourFormat(): Boolean = is24Hour
}
