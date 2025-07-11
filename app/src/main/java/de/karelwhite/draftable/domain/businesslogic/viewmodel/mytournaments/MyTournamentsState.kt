package de.karelwhite.draftable.domain.businesslogic.viewmodel.mytournaments

import de.karelwhite.draftable.domain.model.Host
import de.karelwhite.draftable.domain.model.Tournament

data class MyTournamentsState(
    val host: Host? = null,
    val tournaments: List<Tournament> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val toastMessage: String? = null,
    val isRefreshing: Boolean = false
    )
