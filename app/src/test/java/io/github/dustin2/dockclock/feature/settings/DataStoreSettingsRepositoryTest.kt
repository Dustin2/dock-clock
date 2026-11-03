package io.github.dustin2.dockclock.feature.settings

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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

    @Test
    fun `saves night mode, on by default`() = runTest {
        val repository = repository()
        assertTrue(repository.settings.first().nightMode)

        repository.setNightMode(false)
        assertFalse(repository.settings.first().nightMode)
    }

    @Test
    fun `saves hidden apps`() = runTest {
        val repository = repository()

        repository.setAppHidden("com.example.chat", hidden = true)
        repository.setAppHidden("com.example.mail", hidden = true)
        repository.setAppHidden("com.example.chat", hidden = false)

        assertEquals(setOf("com.example.mail"), repository.settings.first().hiddenApps)
    }
}
