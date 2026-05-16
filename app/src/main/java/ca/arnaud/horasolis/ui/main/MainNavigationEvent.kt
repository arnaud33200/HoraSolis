package ca.arnaud.horasolis.ui.main

sealed interface MainNavigationEvent {
    data object NavigateToAlarmManager : MainNavigationEvent
}
