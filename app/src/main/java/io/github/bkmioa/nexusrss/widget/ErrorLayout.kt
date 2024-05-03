package io.github.bkmioa.nexusrss.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorLayout(message: String, onRetry: () -> Unit) {
    ErrorLayout(Modifier, message, onRetry)
}

@Composable
fun ErrorLayout(modifier: Modifier = Modifier, message: String, onRetry: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.wrapContentWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Button(onClick = onRetry) {
            Text(text = "重试")
        }
    }
}