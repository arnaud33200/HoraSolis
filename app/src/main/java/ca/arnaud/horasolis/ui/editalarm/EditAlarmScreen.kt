package ca.arnaud.horasolis.ui.editalarm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.R

@Composable
fun EditAlarmScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    model: EditAlarmScreenModel,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onBackClick,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back_content_description)
                    )
                }
            }
        },
    ) { innerPadding ->
        when (model) {
            is EditAlarmScreenModel.Content -> EditAlarmContent(
                modifier = Modifier
                    .padding(innerPadding),
                model = model,
            )

            EditAlarmScreenModel.Loading -> Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
            ) {
                // TODO - implement loading state
            }
        }
    }
}

@Composable
private fun EditAlarmContent(
    modifier: Modifier = Modifier,
    model: EditAlarmScreenModel.Content,
) {
    // TODO - implement the content
}
