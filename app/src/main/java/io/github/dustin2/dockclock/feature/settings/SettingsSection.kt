package io.github.dustin2.dockclock.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.dustin2.dockclock.R
import io.github.dustin2.dockclock.core.designsystem.DockClockTheme

@Composable
fun SettingsSection(modifier: Modifier = Modifier, viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsContent(
        hideNotificationContent = uiState.hideNotificationContent,
        onHideNotificationContentChange = viewModel::onHideNotificationContentChange,
        modifier = modifier
    )
}

@Composable
fun SettingsContent(
    hideNotificationContent: Boolean,
    onHideNotificationContentChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        // The whole row toggles, so the target is large and screen readers announce one switch.
        Row(
            modifier = Modifier
                .toggleable(
                    value = hideNotificationContent,
                    role = Role.Switch,
                    onValueChange = onHideNotificationContentChange
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.settings_hide_content_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.settings_hide_content_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = hideNotificationContent, onCheckedChange = null)
        }
    }
}

@Preview
@Composable
private fun SettingsContentPreview() {
    DockClockTheme {
        SettingsContent(hideNotificationContent = true, onHideNotificationContentChange = {})
    }
}
