package de.karelwhite.draftable.viewmodel.mytournaments

import de.karelwhite.draftable.viewmodel.createtournament.CreateTournamentEvent

sealed interface MyTournamentsEvent {
    object LoadTournaments : MyTournamentsEvent
    data class TournamentClicked(val tournamentId: String) : MyTournamentsEvent
    data class DeleteTournament(val tournamentId: String) : MyTournamentsEvent
    object RefreshTournaments : MyTournamentsEvent
    object ToastMessageShown : MyTournamentsEvent
}