package io.github.dustin2.dockclock.feature.appselection

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.dustin2.dockclock.R
import io.github.dustin2.dockclock.core.designsystem.DockClockTheme

@Composable
fun AppSelectionScreen(onBack: () -> Unit, viewModel: AppSelectionViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AppSelectionContent(
        uiState = uiState,
        onBack = onBack,
        onAppShownChange = viewModel::onAppShownChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionContent(
    uiState: AppSelectionUiState,
    onBack: () -> Unit,
    onAppShownChange: (packageName: String, shown: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.choose_apps_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            AppSelectionUiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is AppSelectionUiState.Ready -> LazyColumn(contentPadding = padding) {
                items(uiState.apps, key = { it.packageName }) { app ->
                    AppRow(app, onShownChange = { onAppShownChange(app.packageName, it) })
                }
            }
        }
    }
}

@Composable
private fun AppRow(app: AppSelectionItem, onShownChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = app.isShown, role = Role.Switch, onValueChange = onShownChange)
            .padding(PaddingValues(horizontal = 16.dp, vertical = 12.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val iconModifier = Modifier.size(40.dp)
        val icon = app.icon
        // The label is next to the icon, so the icon itself is decorative.
        if (icon !=
            null
        ) {
            Image(icon, contentDescription = null, modifier = iconModifier)
        } else {
            Spacer(iconModifier)
        }
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = app.isShown, onCheckedChange = null)
    }
}

@Preview
@Composable
private fun AppSelectionContentPreview() {
    DockClockTheme {
        AppSelectionContent(
            uiState = AppSelectionUiState.Ready(
                listOf(
                    AppSelectionItem("a", "Calendar", null, isShown = true),
                    AppSelectionItem("b", "Messages", null, isShown = false)
                )
            ),
            onBack = {},
            onAppShownChange = { _, _ -> }
        )
    }
}
