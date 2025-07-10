package de.karelwhite.draftable.domain.viewmodel.settings

import de.karelwhite.draftable.domain.model.Host

data class SettingsState(
    val host: Host? = null,
    val name: String = "",
    val isLoading : Boolean = true,
    val error : String? = null
)
