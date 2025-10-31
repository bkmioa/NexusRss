@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.bkmioa.nexusrss.detail

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.mavericksViewModel
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.list.ListViewModel
import io.github.bkmioa.nexusrss.list.ThreadList
import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.model.RequestData
import io.github.bkmioa.nexusrss.widget.Empty

@Composable
fun AlternativeVersionDialog(item: Item, onDismissRequest: () -> Unit) {
    val requestData = RequestData(imdb = item.imdb, douban = item.douban)
    val viewModel: ListViewModel = mavericksViewModel(argsFactory = { requestData })

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        modifier = Modifier
            .padding(WindowInsets.statusBars.asPaddingValues())
            .fillMaxHeight()
    ) {
        Text(
            text = stringResource(R.string.alternative_versions),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )
        if (item.imdb.isNullOrBlank() && item.douban.isNullOrBlank()) {
            Empty(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f),
                image = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.empty),
                        contentDescription = "blank",
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                    )
                },
                message = stringResource(R.string.no_content)
            )
        } else {
            ThreadList(requestData = requestData, viewModel = viewModel)
        }
    }
}