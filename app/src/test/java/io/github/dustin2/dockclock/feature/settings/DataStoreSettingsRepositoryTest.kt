package io.github.dustin2.dockclock.feature.settings

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DataStoreSettingsRepositoryTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private fun TestScope.repository() = DataStoreSettingsRepository(
        PreferenceDataStoreFactory.create(scope = backgroundScope) {
            File(tempFolder.root, "settings.preferences_pb")
        }
    )

    @Test
    fun `content is shown by default`() = runTest {
        assertFalse(repository().settings.first().hideNotificationContent)
    }

    @Test
    fun `saves hide notification content`() = runTest {
        val repository = repository()

        repository.setHideNotificationContent(true)
        assertTrue(repository.settings.first().hideNotificationContent)

        repository.setHideNotificationContent(false)
        assertFalse(repository.settings.first().hideNotificationContent)
    }
}
