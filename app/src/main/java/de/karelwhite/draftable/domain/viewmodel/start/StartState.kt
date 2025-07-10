package de.karelwhite.draftable.domain.viewmodel.start

import de.karelwhite.draftable.domain.model.Host

data class StartState(
    val host: Host? = null,
    val isLoading : Boolean = true,
    val error : String? = null
)
