package de.karelwhite.draftable.viewmodel.createtournament

import de.karelwhite.draftable.domain.model.Player


data class CreateTournamentState(
    val tournamentName: String = "",
    val players: List<Player> = emptyList(),
    val numberOfRounds: String = "3",
    val isCreating: Boolean = false,
    val creationError: String? = null,
    val createdTournamentId: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val toastMessage: String? = null
)
