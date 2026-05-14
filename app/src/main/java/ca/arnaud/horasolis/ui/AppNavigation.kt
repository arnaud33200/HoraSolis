package ca.arnaud.horasolis.ui

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import ca.arnaud.horasolis.ui.alarmmanager.AlarmManagerDestination
import ca.arnaud.horasolis.ui.alarmmanager.AlarmManagerViewModel
import ca.arnaud.horasolis.ui.editalarm.EditAlarmDestination

@Composable
fun AppNavigation(alarmManagerViewModel: AlarmManagerViewModel) {
    val backStack = rememberNavBackStack(AppRoute.AlarmManager)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<AppRoute.AlarmManager> {
                AlarmManagerDestination(
                    viewModel = alarmManagerViewModel,
                    onNavigateToEditAlarm = { alarmId ->
                        backStack.add(AppRoute.EditAlarm(alarmId))
                    },
                )
            }
            entry<AppRoute.EditAlarm> { route ->
                EditAlarmDestination(
                    alarmId = route.alarmId,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}
