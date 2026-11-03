package io.github.dustin2.dockclock.feature.clock

import app.cash.turbine.test
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

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `initial state shows current time and date`() {
        val state = ClockViewModel(clock, settings).uiState.value

        assertEquals(ClockUiState(time = "14:05", date = "Tuesday, November 3, 2026"), state)
    }

    @Test
    fun `uses 12 hour format when the device does`() {
        settings.is24Hour = false

        assertEquals("2:05 PM", ClockViewModel(clock, settings).uiState.value.time)
    }

    @Test
    fun `uses the device time zone`() {
        settings.zoneId = ZoneId.of("America/Mexico_City")

        assertEquals("08:05", ClockViewModel(clock, settings).uiState.value.time)
    }

    @Test
    fun `updates exactly at the next minute boundary`() = runTest(dispatcher) {
        ClockViewModel(clock, settings).uiState.test {
            assertEquals("14:05", awaitItem().time)

            advanceTimeByAndRun(29_999)
            expectNoEvents()

            advanceTimeByAndRun(1)
            assertEquals("14:06", awaitItem().time)
        }
    }

    private fun TestScope.advanceTimeByAndRun(millis: Long) {
        advanceTimeBy(millis)
        testScheduler.runCurrent()
    }
}

private class FakeDeviceTimeSettings : DeviceTimeSettings {
    var zoneId: ZoneId = ZoneOffset.UTC
    var is24Hour = true

    override fun zone(): ZoneId = zoneId

    override fun locale(): Locale = Locale.US

    override fun is24HourFormat(): Boolean = is24Hour
}
