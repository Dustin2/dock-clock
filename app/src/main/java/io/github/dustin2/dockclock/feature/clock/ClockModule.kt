package io.github.dustin2.dockclock.feature.clock

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock

@Module
@InstallIn(SingletonComponent::class)
abstract class ClockModule {
    @Binds
    abstract fun bindDeviceTimeSettings(impl: SystemDeviceTimeSettings): DeviceTimeSettings

    companion object {
        // UTC on purpose: the time zone comes from DeviceTimeSettings so zone changes apply live.
        @Provides
        fun provideClock(): Clock = Clock.systemUTC()
    }
}
