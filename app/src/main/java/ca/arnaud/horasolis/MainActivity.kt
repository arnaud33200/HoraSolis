package ca.arnaud.horasolis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {

    val detailViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            HoraSolisTheme {
                val state by detailViewModel.state.collectAsState()

                MainScreen(
                    model = state,
                    onCitySelected = detailViewModel::onCitySelected,
                )
            }
        }
    }
}
