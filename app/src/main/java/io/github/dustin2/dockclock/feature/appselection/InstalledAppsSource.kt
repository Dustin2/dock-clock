package io.github.dustin2.dockclock.feature.appselection

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.ImageBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.dustin2.dockclock.feature.notifications.AppInfoProvider
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class InstalledApp(val packageName: String, val label: String, val icon: ImageBitmap?)

interface InstalledAppsSource {
    /** Apps with a launcher icon, excluding this app. */
    suspend fun launchableApps(): List<InstalledApp>
}

class PackageManagerInstalledAppsSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val appInfoProvider: AppInfoProvider
) : InstalledAppsSource {
    // Querying packages and decoding 100+ icons is too slow for the main thread.
    override suspend fun launchableApps(): List<InstalledApp> = withContext(Dispatchers.IO) {
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        context.packageManager.queryIntentActivities(launcherIntent, 0)
            .map { it.activityInfo.packageName }
            .distinct()
            .filter { it != context.packageName }
            .map { packageName ->
                val info = appInfoProvider.appInfo(packageName)
                InstalledApp(packageName, info.label, info.icon)
            }
    }
}
