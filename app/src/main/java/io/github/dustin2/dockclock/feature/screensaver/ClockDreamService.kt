package io.github.dustin2.dockclock.feature.screensaver

import android.service.dreams.DreamService
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dagger.hilt.android.AndroidEntryPoint
import io.github.dustin2.dockclock.R
import io.github.dustin2.dockclock.core.designsystem.ClockTheme
import io.github.dustin2.dockclock.feature.clock.ClockScreen
import io.github.dustin2.dockclock.feature.clock.ClockViewModel
import io.github.dustin2.dockclock.feature.clock.DeviceTimeSettings
import io.github.dustin2.dockclock.feature.notifications.AppInfoProvider
import io.github.dustin2.dockclock.feature.notifications.NotificationRepository
import io.github.dustin2.dockclock.feature.settings.SettingsRepository
import java.time.Clock
import javax.inject.Inject

/**
 * Shows the clock as the system screen saver, which Android starts by itself while charging.
 *
 * Unlike an activity, a DreamService provides no lifecycle, saved state or ViewModelStore,
 * which Compose needs, so this class provides them and drives the lifecycle from the dream callbacks.
 */
@AndroidEntryPoint
class ClockDreamService :
    DreamService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {
    @Inject lateinit var clock: Clock

    @Inject lateinit var deviceTimeSettings: DeviceTimeSettings

    @Inject lateinit var notificationRepository: NotificationRepository

    @Inject lateinit var settingsRepository: SettingsRepository

    @Inject lateinit var appInfoProvider: AppInfoProvider

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore = ViewModelStore()
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        // Services have no manifest theme; set it before the dream window is created.
        setTheme(R.style.Theme_DockClock_Clock)
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isFullscreen = true
        // Any touch ends the dream and returns to the phone.
        isInteractive = false

        val viewModel = createClockViewModel()
        setContentView(
            ComposeView(this).apply {
                setContent { ClockTheme { ClockScreen(viewModel) } }
            }
        )
        // Compose looks these up from the window's root view.
        window.decorView.apply {
            setViewTreeLifecycleOwner(this@ClockDreamService)
            setViewTreeViewModelStoreOwner(this@ClockDreamService)
            setViewTreeSavedStateRegistryOwner(this@ClockDreamService)
        }
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onDreamingStopped() {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        super.onDreamingStopped()
    }

    override fun onDestroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        viewModelStore.clear()
        super.onDestroy()
    }

    // hiltViewModel() only works in activities, fragments and navigation entries,
    // so the ViewModel is built here from dependencies Hilt injected into the service.
    private fun createClockViewModel(): ClockViewModel {
        val factory = viewModelFactory {
            initializer {
                ClockViewModel(
                    clock,
                    deviceTimeSettings,
                    notificationRepository,
                    settingsRepository,
                    appInfoProvider
                )
            }
        }
        return ViewModelProvider(this, factory)[ClockViewModel::class.java]
    }
}
