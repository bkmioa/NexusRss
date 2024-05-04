package io.github.bkmioa.nexusrss.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.bkmioa.nexusrss.R

@Composable
fun Empty(
    modifier: Modifier = Modifier,
    image: @Composable () -> Unit = {},
    message: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit) = { }
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        image()

        if (message != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
            )
        }

        if (actionText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            FilledTonalButton(onClick = onAction) {
                Text(text = actionText, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Column {
        Empty(
            image = {
                Icon(
                    modifier = Modifier.size(60.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.wifi_error),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.75f)
                )
            },
            message = "No data available",
            actionText = "Retry",
            onAction = { }
        )
        Empty(
            message = "No data available",
            actionText = "Retry",
            onAction = { }
        )
    }
}