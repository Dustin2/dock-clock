package io.github.dustin2.dockclock.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
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
fun SettingsSection(
    onChooseApps: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsContent(
        settings = uiState,
        onHideNotificationContentChange = viewModel::onHideNotificationContentChange,
        onNightModeChange = viewModel::onNightModeChange,
        onChooseApps = onChooseApps,
        modifier = modifier
    )
}

@Composable
fun SettingsContent(
    settings: UserSettings,
    onHideNotificationContentChange: (Boolean) -> Unit,
    onNightModeChange: (Boolean) -> Unit,
    onChooseApps: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        SwitchRow(
            title = stringResource(R.string.settings_hide_content_title),
            description = stringResource(R.string.settings_hide_content_description),
            checked = settings.hideNotificationContent,
            onCheckedChange = onHideNotificationContentChange
        )
        HorizontalDivider()
        SwitchRow(
            title = stringResource(R.string.settings_night_mode_title),
            description = stringResource(R.string.settings_night_mode_description),
            checked = settings.nightMode,
            onCheckedChange = onNightModeChange
        )
        HorizontalDivider()
        SettingText(
            title = stringResource(R.string.choose_apps_title),
            description = stringResource(R.string.choose_apps_description),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onChooseApps)
                .padding(16.dp)
        )
    }
}

@Composable
private fun SwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // The whole row toggles, so the target is large and screen readers announce one switch.
    Row(
        modifier = Modifier
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingText(title = title, description = description, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = null)
    }
}

@Composable
private fun SettingText(title: String, description: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun SettingsContentPreview() {
    DockClockTheme {
        SettingsContent(
            settings = UserSettings(hideNotificationContent = true),
            onHideNotificationContentChange = {},
            onNightModeChange = {},
            onChooseApps = {}
        )
    }
}
