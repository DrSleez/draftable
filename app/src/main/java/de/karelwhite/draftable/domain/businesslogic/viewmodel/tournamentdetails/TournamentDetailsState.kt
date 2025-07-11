package de.karelwhite.draftable.domain.businesslogic.viewmodel.tournamentdetails

import de.karelwhite.draftable.domain.model.Match
import de.karelwhite.draftable.domain.model.Tournament

data class TournamentDetailsState(
    val tournament: Tournament? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLeaderboardVisible: Boolean = false,
    val isMatchResultDialogVisible: Boolean = false,
    val selectedMatchForDialog: Match? = null
)
