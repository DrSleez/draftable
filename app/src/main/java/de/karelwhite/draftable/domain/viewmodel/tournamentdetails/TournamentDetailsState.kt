package de.karelwhite.draftable.domain.viewmodel.tournamentdetails

import de.karelwhite.draftable.domain.model.Host
import de.karelwhite.draftable.domain.model.Tournament

data class TournamentDetailsState(
    val tournament: Tournament? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
