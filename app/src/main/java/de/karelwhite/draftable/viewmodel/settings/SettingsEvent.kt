package de.karelwhite.draftable.viewmodel.settings

sealed interface SettingsEvent {
    object SaveSettings : SettingsEvent
    data class SetName(val name: String) : SettingsEvent
    object LoadInitialSettings : SettingsEvent
}