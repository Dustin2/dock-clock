package io.github.dustin2.dockclock.feature.notifications

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationsModule {
    @Binds
    abstract fun bindNotificationRepository(
        impl: InMemoryNotificationRepository
    ): NotificationRepository

    @Binds
    abstract fun bindAppInfoProvider(impl: PackageManagerAppInfoProvider): AppInfoProvider
}
