package io.github.dustin2.dockclock.feature.notifications

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

data class AppInfo(val label: String, val icon: ImageBitmap?)

interface AppInfoProvider {
    fun appInfo(packageName: String): AppInfo
}

/** Cached per package so the icon is decoded once, not on every clock tick. */
@Singleton
class PackageManagerAppInfoProvider @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AppInfoProvider {
    private val cache = ConcurrentHashMap<String, AppInfo>()

    override fun appInfo(packageName: String): AppInfo = cache.getOrPut(packageName) {
        load(packageName)
    }

    private fun load(packageName: String): AppInfo {
        val packageManager = context.packageManager
        return try {
            val info = packageManager.getApplicationInfo(packageName, 0)
            AppInfo(
                label = packageManager.getApplicationLabel(info).toString(),
                icon = packageManager.getApplicationIcon(
                    info
                ).toBitmap(ICON_SIZE_PX, ICON_SIZE_PX).asImageBitmap()
            )
        } catch (_: PackageManager.NameNotFoundException) {
            // Uninstalled, or hidden from us by package visibility rules.
            AppInfo(label = packageName, icon = null)
        }
    }

    private companion object {
        // Icons are drawn at about 40dp; a fixed small bitmap keeps a list of 100+ apps light on memory.
        const val ICON_SIZE_PX = 128
    }
}
