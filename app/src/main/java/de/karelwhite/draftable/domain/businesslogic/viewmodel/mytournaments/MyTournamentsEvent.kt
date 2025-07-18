package de.karelwhite.draftable.domain.viewmodel.mytournaments

sealed interface MyTournamentsEvent {
    object LoadTournaments : MyTournamentsEvent
    data class DeleteTournament(val tournamentId: String) : MyTournamentsEvent
    object RefreshTournaments : MyTournamentsEvent
    object ToastMessageShown : MyTournamentsEvent
}