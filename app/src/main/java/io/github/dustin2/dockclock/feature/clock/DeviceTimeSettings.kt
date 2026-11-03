package io.github.dustin2.dockclock.feature.clock

import android.content.Context
import android.text.format.DateFormat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
import java.util.Locale
import javax.inject.Inject

/** User time preferences from the device. Read on every tick because the user can change them at any time. */
interface DeviceTimeSettings {
    fun zone(): ZoneId

    fun locale(): Locale

    fun is24HourFormat(): Boolean
}

class SystemDeviceTimeSettings @Inject constructor(
    @param:ApplicationContext private val context: Context
) : DeviceTimeSettings {
    override fun zone(): ZoneId = ZoneId.systemDefault()

    override fun locale(): Locale = Locale.getDefault()

    override fun is24HourFormat(): Boolean = DateFormat.is24HourFormat(context)
}
