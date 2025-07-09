package de.karelwhite.draftable.viewmodel.mytournaments

import de.karelwhite.draftable.domain.model.Host
import de.karelwhite.draftable.domain.model.Tournament

data class MyTournamentsState(
    val host: Host? = null,
    val tournaments: List<Tournament> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val toastMessage: String? = null
    )
