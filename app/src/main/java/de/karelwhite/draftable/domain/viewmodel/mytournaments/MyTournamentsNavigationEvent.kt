package de.karelwhite.draftable.domain.viewmodel.mytournaments

sealed interface MyTournamentsNavigationEvent {
    object TournamentDetails : MyTournamentsNavigationEvent
}
