package de.karelwhite.draftable.domain.viewmodel.createtournament

sealed interface CreateTournamentNavigationEvent {
    object MyTournaments : CreateTournamentNavigationEvent
}