package de.karelwhite.draftable.domain.viewmodel.createtournament

import de.karelwhite.draftable.domain.model.Player

sealed interface CreateTournamentEvent {
    object SaveTournament : CreateTournamentEvent
    data class SetTournamentName(val name: String) : CreateTournamentEvent

    data class AddPlayerByName(val playerName: String) : CreateTournamentEvent

    data class RemovePlayer(val player: Player) : CreateTournamentEvent
    data class SetPlayers(val players: List<Player>) : CreateTournamentEvent

    object LoadInitialCreateTournament : CreateTournamentEvent

    object ToastMessageShown : CreateTournamentEvent
}