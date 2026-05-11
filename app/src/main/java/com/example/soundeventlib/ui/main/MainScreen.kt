package com.example.soundeventlib.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import io.github.eescobar.soundeventlib.SoundEvent
import io.github.eescobar.soundeventlib.SoundManager
import com.example.soundeventlib.theme.MyApplicationTheme

@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    SoundDemoContent(modifier = modifier.fillMaxSize())
}

@Composable
private fun SoundDemoContent(modifier: Modifier = Modifier) {
    var soundEnabled by remember { mutableStateOf(true) }
    var volume by remember { mutableFloatStateOf(0.9f) }

    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Sound Event Library Demo", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Sounds enabled")
            Switch(
                checked = soundEnabled,
                onCheckedChange = { enabled ->
                    soundEnabled = enabled
                    SoundManager.setEnabled(enabled)
                },
            )
        }

        Column {
            Text("Volume: ${"%.0f".format(volume * 100)}%")
            Slider(
                value = volume,
                onValueChange = { v ->
                    volume = v
                    SoundManager.setVolume(v)
                },
                valueRange = 0f..1f,
            )
        }

        Spacer(Modifier.height(8.dp))

        Text("Trigger events:", style = MaterialTheme.typography.titleMedium)

        listOf(
            "SUCCESS" to SoundEvent.SUCCESS,
            "ERROR" to SoundEvent.ERROR,
            "WARNING" to SoundEvent.WARNING,
            "SCAN DETECTED" to SoundEvent.SCAN_DETECTED,
            "NOTIFICATION" to SoundEvent.NOTIFICATION,
        ).forEach { (label, event) ->
            Button(
                onClick = { SoundManager.play(event) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(label)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MyApplicationTheme { SoundDemoContent() }
}
