package io.github.dustin2.dockclock.feature.permissions

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.dustin2.dockclock.feature.notifications.DockClockNotificationListener
import javax.inject.Inject

interface NotificationAccessChecker {
    fun isGranted(): Boolean
}

class SystemNotificationAccessChecker @Inject constructor(
    @param:ApplicationContext private val context: Context
) : NotificationAccessChecker {
    override fun isGranted(): Boolean =
        context.packageName in NotificationManagerCompat.getEnabledListenerPackages(context)
}

/**
 * Opens our own toggle on Android 11+, or the general list on older versions
 * and on devices without the detail screen.
 */
fun Context.openNotificationAccessSettings() {
    val generalSettings = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        startActivity(generalSettings)
        return
    }
    val component = ComponentName(this, DockClockNotificationListener::class.java)
    val detailSettings =
        Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)
            .putExtra(
                Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME,
                component.flattenToString()
            )
    try {
        startActivity(detailSettings)
    } catch (_: ActivityNotFoundException) {
        startActivity(generalSettings)
    }
}
