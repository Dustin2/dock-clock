package io.github.dustin2.dockclock.feature.permissions

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PermissionsModule {
    @Binds
    abstract fun bindNotificationAccessChecker(
        impl: SystemNotificationAccessChecker
    ): NotificationAccessChecker
}
