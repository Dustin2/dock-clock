package io.github.dustin2.dockclock.feature.permissions

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionsViewModelTest {
    private val checker = FakeNotificationAccessChecker()

    @Test
    fun `initial state reflects current access`() {
        checker.granted = true

        assertTrue(PermissionsViewModel(checker).uiState.value.hasNotificationAccess)
    }

    @Test
    fun `onResume picks up access granted in Settings`() {
        val viewModel = PermissionsViewModel(checker)

        checker.granted = true
        viewModel.onResume()

        assertTrue(viewModel.uiState.value.hasNotificationAccess)
    }

    @Test
    fun `onResume picks up revoked access`() {
        checker.granted = true
        val viewModel = PermissionsViewModel(checker)

        checker.granted = false
        viewModel.onResume()

        assertFalse(viewModel.uiState.value.hasNotificationAccess)
    }
}

private class FakeNotificationAccessChecker : NotificationAccessChecker {
    var granted = false

    override fun isGranted(): Boolean = granted
}
