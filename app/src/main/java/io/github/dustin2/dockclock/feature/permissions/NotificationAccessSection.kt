package io.github.dustin2.dockclock.feature.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.dustin2.dockclock.R
import io.github.dustin2.dockclock.core.designsystem.DockClockTheme

@Composable
fun NotificationAccessSection(
    modifier: Modifier = Modifier,
    viewModel: PermissionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LifecycleResumeEffect(viewModel) {
        viewModel.onResume()
        onPauseOrDispose { }
    }
    NotificationAccessContent(
        hasAccess = uiState.hasNotificationAccess,
        onOpenSettings = { context.openNotificationAccessSettings() },
        modifier = modifier
    )
}

@Composable
fun NotificationAccessContent(
    hasAccess: Boolean,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.notification_access_title),
                style = MaterialTheme.typography.titleMedium
            )
            if (hasAccess) {
                Text(stringResource(R.string.notification_access_granted))
            } else {
                Text(stringResource(R.string.notification_access_rationale))
                Button(onClick = onOpenSettings) {
                    Text(stringResource(R.string.notification_access_open_settings))
                }
            }
        }
    }
}

@Preview
@Composable
private fun NotificationAccessMissingPreview() {
    DockClockTheme { NotificationAccessContent(hasAccess = false, onOpenSettings = {}) }
}

@Preview
@Composable
private fun NotificationAccessGrantedPreview() {
    DockClockTheme { NotificationAccessContent(hasAccess = true, onOpenSettings = {}) }
}
